package com.example.followup.service;

import com.example.followup.dto.response.PatientVO;
import com.example.followup.entity.Patient;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PatientMaskingServiceTest {
    private final PatientMaskingService service=new PatientMaskingService();
    @Test void adminReceivesOriginalFields(){ Patient p=patient(); PatientVO v=service.toVO(p,true); assertAll(()->assertEquals(p.getName(),v.getName()),()->assertEquals(p.getPhone(),v.getPhone()),()->assertEquals(p.getIdCard(),v.getIdCard()),()->assertEquals(p.getAddress(),v.getAddress())); }
    @Test void doctorReceivesMaskedFields(){ PatientVO v=service.toVO(patient(),false); assertAll(()->assertEquals("张**",v.getName()),()->assertEquals("138****5678",v.getPhone()),()->assertEquals("320102********1234",v.getIdCard()),()->assertEquals("南京市鼓楼区****",v.getAddress())); }
    @Test void conversionDoesNotModifyEntity(){ Patient p=patient(); service.toVO(p,false); assertAll(()->assertEquals("张三丰",p.getName()),()->assertEquals("13812345678",p.getPhone()),()->assertEquals("320102199001011234",p.getIdCard())); }
    @Test void supportsFifteenDigitId(){ assertEquals("320102*****1234",service.maskIdCard("320102900101234")); }
    @Test void handlesNullAndEmpty(){ assertAll(()->assertNull(service.maskName(null)),()->assertEquals("",service.maskPhone("")),()->assertNull(service.maskAddress(null))); }
    @Test void shortMalformedValuesAreSafelyMasked(){ assertAll(()->assertEquals("**",service.maskPhone("12")),()->assertEquals("***",service.maskIdCard("abc")),()->assertEquals("****",service.maskAddress("短地址"))); }
    private Patient patient(){ Patient p=new Patient();p.setId(1L);p.setName("张三丰");p.setPhone("13812345678");p.setIdCard("320102199001011234");p.setAddress("南京市鼓楼区汉口路22号");p.setDoctorId(7L);p.setStatus(1);return p; }
}
