import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    token: null,
    isLoggedIn: false
  }),
  actions: {
    // 在 user.js 中
async login(account, password) {
  try {
    const response = await axios.post('/api/user/login', { account, password })
    this.token = response.data.data
    this.isLoggedIn = true
    localStorage.setItem('token', response.data.data)
    axios.defaults.headers.common['Authorization'] = `Bearer ${response.data.data}`
    await this.fetchUserInfo()
    return true
  } catch (error) {
    console.error('Login failed:', error)
    return false
  }
},
    async register(account, password, confirmPassword) {
      try {
        await axios.post('/api/user/register', { account, password, confirmPassword })
        return true
      } catch (error) {
        console.error('Registration failed:', error)
        return false
      }
    },
    async updatePassword(oldPwd, newPwd, rePwd) {
      try {
        await axios.post('/api/user/updatePwd', { oldPwd, newPwd, rePwd })
        return true
      } catch (error) {
        console.error('Password update failed:', error)
        return false
      }
    },
    async fetchUserInfo() {
      try {
        const response = await axios.get('/api/student/info')
        this.user = response.data.data
        return true
      } catch (error) {
        console.error('Fetch user info failed:', error)
        return false
      }
    },
    logout() {
      this.user = null
      this.token = null
      this.isLoggedIn = false
      localStorage.removeItem('token')
      delete axios.defaults.headers.common['Authorization']
    }
  }
})