package com.example.followup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.followup.dto.request.PatientQuery;
import com.example.followup.dto.response.PageResponse;
import com.example.followup.dto.response.PatientVO;
import com.example.followup.entity.FollowUp;
import com.example.followup.entity.Patient;
import com.example.followup.entity.SysUser;
import com.example.followup.exception.BusinessException;
import com.example.followup.exception.ErrorCode;
import com.example.followup.mapper.FollowUpMapper;
import com.example.followup.mapper.PatientMapper;
import com.example.followup.mapper.SysUserMapper;
import com.example.followup.service.PatientService;
import com.example.followup.util.DesensitizationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private FollowUpMapper followUpMapper;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public PageResponse<PatientVO> listPatients(PatientQuery query) {
        LambdaQueryWrapper<Patient> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Patient::getStatus, 1);
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(Patient::getName, query.getName());
        }
        if (StringUtils.hasText(query.getDiseaseType())) {
            wrapper.eq(Patient::getDiseaseType, query.getDiseaseType());
        }
        if (!isAdmin()) {
            wrapper.eq(Patient::getDoctorId, currentUserId());
        }
        wrapper.orderByDesc(Patient::getCreateTime);

        Page<Patient> page = new Page<>(query.getPage(), query.getSize());
        patientMapper.selectPage(page, wrapper);

        List<PatientVO> vos = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        enrich(vos);

        boolean admin = isAdmin();
        if (!admin) {
            vos.forEach(this::desensitize);
        }

        PageResponse<PatientVO> response = PageResponse.of(page, query.getPage(), query.getSize());
        response.setRecords(vos);
        return response;
    }

    @Override
    public PatientVO getPatientById(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null || patient.getStatus() == 0) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
        if (!isAdmin() && !Objects.equals(patient.getDoctorId(), currentUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        PatientVO vo = toVO(patient);
        enrich(List.of(vo));
        if (!isAdmin()) {
            desensitize(vo);
        }
        return vo;
    }

    @Override
    public void addPatient(Patient patient) {
        patient.setId(null);
        patient.setStatus(1);
        patientMapper.insert(patient);
    }

    @Override
    public void updatePatient(Patient patient) {
        getPatientById(patient.getId());
        patientMapper.updateById(patient);
    }

    @Override
    public void deletePatient(Long id) {
        Patient patient = patientMapper.selectById(id);
        if (patient == null || patient.getStatus() == 0) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }
        if (!isAdmin() && !Objects.equals(patient.getDoctorId(), currentUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        patient.setStatus(0);
        patientMapper.updateById(patient);
    }

    private PatientVO toVO(Patient patient) {
        PatientVO vo = new PatientVO();
        BeanUtils.copyProperties(patient, vo);
        return vo;
    }

    private void enrich(List<PatientVO> vos) {
        if (vos.isEmpty()) {
            return;
        }

        List<Long> patientIds = vos.stream()
                .map(PatientVO::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (!patientIds.isEmpty()) {
            Map<Long, LocalDate> latestFollowUpMap = followUpMapper.selectList(
                            new LambdaQueryWrapper<FollowUp>()
                                    .in(FollowUp::getPatientId, patientIds)
                                    .isNotNull(FollowUp::getFollowUpDate)
                                    .select(FollowUp::getPatientId, FollowUp::getFollowUpDate))
                    .stream()
                    .collect(Collectors.toMap(
                            FollowUp::getPatientId,
                            FollowUp::getFollowUpDate,
                            (a, b) -> a.isAfter(b) ? a : b
                    ));
            vos.forEach(vo -> vo.setLastFollowUpDate(latestFollowUpMap.get(vo.getId())));
        }

        List<Long> doctorIds = vos.stream()
                .map(PatientVO::getDoctorId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (!doctorIds.isEmpty()) {
            Map<Long, String> doctorNameMap = sysUserMapper.selectBatchIds(doctorIds).stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName, (a, b) -> a));
            vos.forEach(vo -> vo.setDoctorName(doctorNameMap.get(vo.getDoctorId())));
        }
    }

    private void desensitize(PatientVO vo) {
        vo.setName(DesensitizationUtil.maskName(vo.getName()));
        vo.setPhone(DesensitizationUtil.maskPhone(vo.getPhone()));
        vo.setIdCard(DesensitizationUtil.maskIdCard(vo.getIdCard()));
        vo.setAddress(DesensitizationUtil.maskAddress(vo.getAddress()));
    }

    private Long currentUserId() {
        String username = currentUsername();
        SysUser user = sysUserMapper.findByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return user.getId();
    }

    private String currentUsername() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "";
        }
        HttpServletRequest request = attributes.getRequest();
        Object username = request.getAttribute("username");
        return username == null ? "" : String.valueOf(username);
    }

    private boolean isAdmin() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            return "ADMIN".equals(request.getAttribute("role"));
        }
        return false;
    }
}
