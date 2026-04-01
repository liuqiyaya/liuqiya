<template>
  <div class="device-type-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备类型</span>
        </div>
      </template>
      <el-table :data="deviceTypes" style="width: 100%">
        <el-table-column prop="id" label="设备类型ID" width="120" />
        <el-table-column prop="name" label="设备类型名称" />
        <el-table-column prop="description" label="设备类型描述" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDeviceTypes } from '../../services/api'

const deviceTypes = ref([])

const fetchDeviceTypes = async () => {
  try {
    const response = await getDeviceTypes()
    deviceTypes.value = response.data.data
  } catch (error) {
    console.error('Fetch device types failed:', error)
    ElMessage.error('获取设备类型失败')
  }
}

onMounted(async () => {
  await fetchDeviceTypes()
})
</script>

<style scoped>
.device-type-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>