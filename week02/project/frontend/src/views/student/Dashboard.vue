<template>
  <div class="dashboard-container">
    <el-container>
      <el-header height="60px">
        <div class="header-content">
          <h1 class="title">宿舍报修系统</h1>
          <div class="user-info">
            <span>欢迎，{{ user?.account }}</span>
            <el-button type="text" @click="handleLogout">退出登录</el-button>
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px" class="aside">
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical-demo"
            router
          >
            <el-menu-item index="/student">
              <el-icon><House /></el-icon>
              <span>仪表盘</span>
            </el-menu-item>
            <el-menu-item index="/student/dormitory">
              <el-icon><OfficeBuilding /></el-icon>
              <span>宿舍管理</span>
            </el-menu-item>
            <el-menu-item index="/student/repair">
              <el-icon><Tools /></el-icon>
              <span>报修管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { House, OfficeBuilding, Tools } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const activeMenu = ref('/student')

const user = computed(() => userStore.user)

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('退出登录成功')
  router.push('/login')
}

onMounted(async () => {
  if (!user.value) {
    await userStore.fetchUserInfo()
  }
})
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
  overflow: hidden;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  background-color: #409eff;
  color: white;
}

.title {
  font-size: 20px;
  font-weight: bold;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.aside {
  background-color: #f5f7fa;
  border-right: 1px solid #e4e7ed;
}

.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}
</style>