<template>
  <div class="student-dashboard">
    <!-- 添加顶部用户信息栏 -->
    <div class="top-bar">
      <div class="user-info">
        <span>欢迎，{{ user?.account }}</span>
        <el-button type="text" @click="showPwdDialog = true">修改密码</el-button>
        <el-button type="text" @click="handleLogout">退出登录</el-button>
      </div>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
        </div>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="账号">{{ user?.account }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ user?.role === 'student' ? '学生' : '维修人员' }}</el-descriptions-item>
        <el-descriptions-item label="宿舍楼">{{ user?.dormBuilding || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="宿舍号">{{ user?.dormRoom || '未绑定' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="mt-4">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card @click="navigateTo('/student/dormitory')" class="cursor-pointer">
            <div class="action-card">
              <el-icon class="action-icon"><OfficeBuilding /></el-icon>
              <div class="action-text">宿舍管理</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card @click="navigateTo('/student/repair')" class="cursor-pointer">
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
import { computed, ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import { useUserStore } from '../../store/user'
import { OfficeBuilding, Tools } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const user = computed(() => userStore.user)
const showPwdDialog = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref(null)

const pwdForm = reactive({
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
        if (value !== pwdForm.newPwd) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
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
          oldPwd: pwdForm.oldPwd,
          newPwd: pwdForm.newPwd,
          rePwd: pwdForm.rePwd
        })
        ElMessage.success('密码修改成功，请重新登录')
        showPwdDialog.value = false
        
        // 清空表单
        pwdForm.oldPwd = ''
        pwdForm.newPwd = ''
        pwdForm.rePwd = ''
        
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
</script>

<style scoped>
.student-dashboard {
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