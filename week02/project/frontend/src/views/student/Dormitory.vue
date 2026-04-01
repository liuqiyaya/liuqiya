<template>
  <div class="dormitory-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>宿舍管理</span>
          <el-button type="primary" @click="showBindDialog = true">
            <el-icon><Edit /></el-icon>
            <span>绑定/更新宿舍</span>
          </el-button>
        </div>
      </template>
      <el-table :data="dormitories" style="width: 100%">
        <el-table-column prop="id" label="宿舍ID" width="100" />
        <el-table-column prop="building" label="宿舍楼" />
        <el-table-column prop="room" label="宿舍号" width="100" />
        <el-table-column prop="capacity" label="容量" width="80" />
        <el-table-column prop="currentCount" label="当前人数" width="100" />
      </el-table>
    </el-card>

    <!-- 绑定/更新宿舍对话框 -->
    <el-dialog
      v-model="showBindDialog"
      title="绑定/更新宿舍"
      width="400px"
    >
      <el-form :model="bindForm" :rules="bindRules" ref="bindFormRef" label-width="80px">
        <el-form-item label="宿舍楼" prop="building">
          <el-input v-model="bindForm.building"></el-input>
        </el-form-item>
        <el-form-item label="宿舍号" prop="room">
          <el-input v-model="bindForm.room"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showBindDialog = false">取消</el-button>
          <el-button type="primary" @click="bindDormitory">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getDormitories, bindDormitory as bindDorm } from '../../services/api'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
const dormitories = ref([])
const showBindDialog = ref(false)
const bindFormRef = ref(null)

const bindForm = reactive({
  building: '',
  room: ''
})

const bindRules = {
  building: [
    { required: true, message: '请输入宿舍楼', trigger: 'blur' }
  ],
  room: [
    { required: true, message: '请输入宿舍号', trigger: 'blur' }
  ]
}

const fetchDormitories = async () => {
  try {
    const response = await getDormitories()
    dormitories.value = response.data.data
  } catch (error) {
    console.error('Fetch dormitories failed:', error)
    ElMessage.error('获取宿舍列表失败')
  }
}

const bindDormitory = async () => {
  if (!bindFormRef.value) return
  await bindFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await bindDorm(bindForm.building, bindForm.room)
        ElMessage.success('绑定/更新宿舍成功')
        showBindDialog.value = false
        // 刷新用户信息
        await userStore.fetchUserInfo()
      } catch (error) {
        console.error('Bind dormitory failed:', error)
        ElMessage.error('绑定/更新宿舍失败')
      }
    }
  })
}

onMounted(async () => {
  await fetchDormitories()
  // 预填当前宿舍信息
  if (userStore.user) {
    bindForm.building = userStore.user.dormBuilding || ''
    bindForm.room = userStore.user.dormRoom || ''
  }
})
</script>

<style scoped>
.dormitory-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>