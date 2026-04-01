<template>
  <div class="admin-dashboard">
    <!-- 添加顶部用户信息栏 -->
    <div class="top-bar">
      <div class="user-info">
        <span>欢迎，{{ adminAccount }}</span>
        <el-button type="text" @click="showPwdDialog = true">修改密码</el-button>
        <el-button type="text" @click="handleLogout">退出登录</el-button>
      </div>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统概览</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ totalRepairs }}</div>
              <div class="stat-label">总报修单</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ pendingRepairs }}</div>
              <div class="stat-label">待处理</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ completedRepairs }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="mt-4">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card @click="navigateTo('/admin/repair')" class="cursor-pointer">
            <div class="action-card">
              <el-icon class="action-icon"><Tools /></el-icon>
              <div class="action-text">报修管理</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPwdDialog" title="修改密码" width="400px">
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="80px">
        <el-form-item label="原密码" prop="oldPwd">
          <el-input v-model="pwdForm.oldPwd" type="password" placeholder="请输入原密码"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPwd">
          <el-input v-model="pwdForm.newPwd" type="password" placeholder="请输入新密码（5-16位）"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="rePwd">
          <el-input v-model="pwdForm.rePwd" type="password" placeholder="请再次输入新密码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" @click="updatePassword" :loading="pwdLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import { Tools } from '@element-plus/icons-vue'
import { getAllRepairOrders } from '../../services/api'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const totalRepairs = ref(0)
const pendingRepairs = ref(0)
const completedRepairs = ref(0)

// 获取管理员账号
const adminAccount = computed(() => userStore.user?.account || '管理员')

// 修改密码相关
const showPwdDialog = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref(null)

const pwdForm = ref({
  oldPwd: '',
  newPwd: '',
  rePwd: ''
})

// 密码校验规则
const pwdRules = {
  oldPwd: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPwd: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 5, max: 16, message: '密码长度5-16位', trigger: 'blur' }
  ],
  rePwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== pwdForm.value.newPwd) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const fetchStats = async () => {
  try {
    const response = await getAllRepairOrders()
    const repairs = response.data.data
    totalRepairs.value = repairs.length
    pendingRepairs.value = repairs.filter(r => r.status === '待处理').length
    completedRepairs.value = repairs.filter(r => r.status === '已完成').length
  } catch (error) {
    console.error('Fetch stats failed:', error)
    ElMessage.error('获取统计数据失败')
  }
}

const navigateTo = (path) => {
  router.push(path)
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

// 修改密码
const updatePassword = async () => {
  if (!pwdFormRef.value) return
  
  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      pwdLoading.value = true
      try {
        await request.post('/user/updatePwd', {
          oldPwd: pwdForm.value.oldPwd,
          newPwd: pwdForm.value.newPwd,
          rePwd: pwdForm.value.rePwd
        })
        ElMessage.success('密码修改成功，请重新登录')
        showPwdDialog.value = false
        
        // 清空表单
        pwdForm.value = {
          oldPwd: '',
          newPwd: '',
          rePwd: ''
        }
        
        // 退出登录
        setTimeout(() => {
          userStore.logout()
          router.push('/login')
        }, 1500)
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '修改失败')
      } finally {
        pwdLoading.value = false
      }
    }
  })
}

onMounted(async () => {
  await fetchStats()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

/* 顶部栏样式 */
.top-bar {
  margin-bottom: 20px;
  padding: 10px 20px;
  background: #f5f7fa;
  border-radius: 8px;
  display: flex;
  justify-content: flex-end;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info span {
  color: #333;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-4 {
  margin-top: 20px;
}

.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-content {
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 16px;
  color: #606266;
}

.cursor-pointer {
  cursor: pointer;
  transition: all 0.3s;
}

.cursor-pointer:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.action-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 10px;
}

.action-text {
  font-size: 16px;
  font-weight: bold;
}
</style>