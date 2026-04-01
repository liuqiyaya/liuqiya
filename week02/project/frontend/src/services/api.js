import request from '../utils/request'

// 认证相关
export const login = (account, password) => {
  return request.post('/user/login', { account, password })
}

export const register = (account, password, confirmPassword) => {
  return request.post('/user/register', { account, password, confirmPassword })
}

export const updatePassword = (oldPwd, newPwd, rePwd) => {
  return request.post('/user/updatePwd', { oldPwd, newPwd, rePwd })
}

// 学生相关
export const getStudentInfo = () => {
  return request.get('/student/info')
}

export const getDormitories = () => {
  return request.get('/student/dormitory')
}

export const bindDormitory = (building, room) => {
  return request.post('/student/dormitory', { building, room })
}

export const getDeviceTypes = () => {
  return request.get('/student/deviceTypes')
}

export const createRepairOrder = (data) => {
  return request.post('/student/repairs', data)
}

export const getStudentRepairOrders = () => {
  return request.get('/student/repairs')
}

export const getRepairOrderDetail = (id) => {
  return request.get(`/student/repairs/${id}`)
}

export const cancelRepairOrder = (id) => {
  return request.delete(`/student/repair/${id}`)
}

// 管理员相关
export const getAllRepairOrders = () => {
  return request.get('/admin/repairs')
}

export const getAdminRepairOrderDetail = (id) => {
  return request.get(`/admin/repairs/${id}`)
}

export const updateRepairOrderStatus = (id, status) => {
  return request.post(`/admin/repairs/${id}`, { status })
}

export const deleteRepairOrder = (id) => {
  return request.delete(`/admin/repairs/${id}`)
}

// 按状态查询报修单
export const getRepairOrdersByStatus = (status) => {
  return request.get('/admin/repairs/status', { params: { status } })
}