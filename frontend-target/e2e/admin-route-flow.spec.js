import { expect, test } from '@playwright/test'

test('login, logout and protected-route redirect form a browser security loop', async ({ page }) => {
  const username = process.env.E2E_ADMIN_USERNAME
  const password = process.env.E2E_ADMIN_PASSWORD
  if (!username || !password) throw new Error('E2E_ADMIN_USERNAME and E2E_ADMIN_PASSWORD are required')

  await page.goto('/login')
  await page.getByPlaceholder('用户名').fill(username)
  await page.getByPlaceholder('密码').fill(password)
  await page.getByRole('button', { name: '登录' }).click()
  await expect(page).toHaveURL(/\/dashboard$/)
  await page.getByRole('button', { name: '退出', exact: true }).click()
  await expect(page).toHaveURL(/\/login$/)
  await page.goto('/patients')
  await expect(page).toHaveURL(/\/login$/)
})
