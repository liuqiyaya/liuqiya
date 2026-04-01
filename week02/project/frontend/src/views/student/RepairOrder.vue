<template>
  <div class="repair-order-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报修管理</span>
          <el-button type="primary" @click="showCreateDialog = true">
            <el-icon><Plus /></el-icon>
            <span>创建报修单</span>
          </el-button>
        </div>
      </template>
      <el-table :data="repairOrders" style="width: 100%">
        <el-table-column prop="id" label="报修单号" width="120" />
        <el-table-column prop="dormBuilding" label="宿舍楼" />
        <el-table-column prop="dormRoom" label="宿舍号" width="100" />
        <el-table-column prop="problemDescription" label="问题描述" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewRepairOrder(scope.row.id)">
              查看
            </el-button>
            <el-button
              v-if="scope.row.status === '待处理'"
              type="danger"
              size="small"
              @click="cancelRepairOrder(scope.row.id)"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建报修单对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建报修单"
      width="500px"
      @close="resetRepairForm"
    >
      <el-form :model="repairForm" :rules="repairRules" ref="repairFormRef" label-width="100px">
        <el-form-item label="宿舍楼" prop="dormBuilding">
          <el-input v-model="repairForm.dormBuilding"></el-input>
        </el-form-item>
        <el-form-item label="宿舍号" prop="dormRoom">
          <el-input v-model="repairForm.dormRoom"></el-input>
        </el-form-item>
        <el-form-item label="设备类型" prop="deviceTypeId">
        <el-select v-model="repairForm.deviceTypeId" placeholder="请选择设备类型">
        <el-option
         v-for="deviceType in deviceTypes"
      :key="deviceType.id"
      :label="`${deviceType.id} - ${deviceType.typeName}`"
      :value="deviceType.id"
    ></el-option>
  </el-select>
</el-form-item>
        <el-form-item label="问题描述" prop="problemDescription">
          <el-input v-model="repairForm.problemDescription" type="textarea" rows="3"></el-input>
        </el-form-item>

       <el-form-item label="图片" prop="imageUrl">
  <el-upload
    class="upload-demo"
    action="/api/student/upload"
    :headers="uploadHeaders"
    :show-file-list="false"
    :on-success="handleUploadSuccess"
    :before-upload="beforeUpload"
    accept="image/*"
  >
    <el-button type="primary">点击上传</el-button>
    <template #tip>
      <div class="el-upload__tip">只能上传图片文件，且不超过2MB</div>
    </template>
  </el-upload>
  <div v-if="repairForm.imageUrl" style="margin-top: 10px;">
    <img :src="repairForm.imageUrl" alt="图片预览" style="max-width: 100px;" />
  </div>
</el-form-item>

      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="() => { showCreateDialog = false; resetRepairForm() }">取消</el-button>
          <el-button type="primary" @click="createRepairOrder">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看报修单对话框 -->
<el-dialog
  v-model="showViewDialog"
  title="报修单详情"
  width="500px"
>
  <el-form :model="currentRepairOrder" label-width="100px">
    <el-form-item label="报修单号">
      <el-input v-model="currentRepairOrder.id" disabled></el-input>
    </el-form-item>
    <el-form-item label="宿舍楼">
      <el-input v-model="currentRepairOrder.dormBuilding" disabled></el-input>
    </el-form-item>
    <el-form-item label="宿舍号">
      <el-input v-model="currentRepairOrder.dormRoom" disabled></el-input>
    </el-form-item>
    <el-form-item label="设备类型">
      <el-input v-model="currentRepairOrder.deviceTypeName" disabled></el-input>
    </el-form-item>
    <el-form-item label="问题描述">
      <el-input v-model="currentRepairOrder.problemDescription" type="textarea" rows="3" disabled></el-input>
    </el-form-item>
    
    <!-- 图片显示（修改这里） -->
    <el-form-item label="报修图片">
      <div v-if="currentRepairOrder.imageUrl">
        <el-image 
          :src="currentRepairOrder.imageUrl" 
          :preview-src-list="[currentRepairOrder.imageUrl]"
          fit="cover"
          style="width: 200px; height: 150px; cursor: pointer;"
        />
        <div style="margin-top: 5px; font-size: 12px; color: #999;">
          点击图片可放大查看
        </div>
      </div>
      <div v-else style="color: #999;">暂无图片</div>
    </el-form-item>
    
    <el-form-item label="状态">
      <el-tag :type="getStatusType(currentRepairOrder.status)">
        {{ currentRepairOrder.status }}
      </el-tag>
    </el-form-item>
    <el-form-item label="创建时间">
      <el-input v-model="currentRepairOrder.createTime" disabled></el-input>
    </el-form-item>
  </el-form>
</el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  getStudentRepairOrders,
  getDeviceTypes,
  createRepairOrder as createRepair,
  getRepairOrderDetail,
  cancelRepairOrder as cancelRepair
} from '../../services/api'

const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

const resetRepairForm = () => {
  repairForm.dormBuilding = ''
  repairForm.dormRoom = ''
  repairForm.deviceTypeId = ''
  repairForm.problemDescription = ''
  repairForm.imageUrl = ''
  if (repairFormRef.value) {
    repairFormRef.value.resetFields()
  }
}

const repairOrders = ref([])
const deviceTypes = ref([])
const showCreateDialog = ref(false)
const showViewDialog = ref(false)
const repairFormRef = ref(null)

const repairForm = reactive({
  dormBuilding: '',
  dormRoom: '',
  deviceTypeId: '',
  problemDescription: '',
  imageUrl: ''
})

const currentRepairOrder = reactive({
  id: '',
  dormBuilding: '',
  dormRoom: '',
  deviceTypeId: '',
  deviceTypeName: '',
  problemDescription: '',
  imageUrl: '',
  status: '',
  createTime: ''
})

const repairRules = {
  dormBuilding: [
    { required: true, message: '请输入宿舍楼', trigger: 'blur' }
  ],
  dormRoom: [
    { required: true, message: '请输入宿舍号', trigger: 'blur' }
  ],
  deviceTypeId: [
    { required: true, message: '请选择设备类型', trigger: 'blur' }
  ],
  problemDescription: [
    { required: true, message: '请输入问题描述', trigger: 'blur' }
  ]
}

const getStatusType = (status) => {
  switch (status) {
    case '待处理':
      return 'warning'
    case '处理中':
      return 'info'
    case '已完成':
      return 'success'
    case '已取消':
      return 'danger'
    default:
      return ''
  }
}

const fetchRepairOrders = async () => {
  try {
    const response = await getStudentRepairOrders()
    repairOrders.value = response.data.data
  } catch (error) {
    console.error('Fetch repair orders failed:', error)
    ElMessage.error('获取报修单失败')
  }
}

const fetchDeviceTypes = async () => {
  try {
    const response = await getDeviceTypes()
    deviceTypes.value = response.data.data
  } catch (error) {
    console.error('Fetch device types failed:', error)
    ElMessage.error('获取设备类型失败')
  }
}

const createRepairOrder = async () => {
  if (!repairFormRef.value) return
  await repairFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await createRepair(repairForm)
        ElMessage.success('创建报修单成功')
        showCreateDialog.value = false
        await fetchRepairOrders()
      } catch (error) {
        console.error('Create repair order failed:', error)
        ElMessage.error('创建报修单失败')
      }
    }
  })
}

const viewRepairOrder = async (id) => {
  try {
    const response = await getRepairOrderDetail(id)
    const repairOrder = response.data.data
    Object.assign(currentRepairOrder, repairOrder)
    
    // 获取设备类型名称
    const deviceType = deviceTypes.value.find(dt => dt.id === repairOrder.deviceTypeId)
    currentRepairOrder.deviceTypeName = deviceType ? deviceType.typeName : ''
    
    showViewDialog.value = true
  } catch (error) {
    console.error('View repair order failed:', error)
    ElMessage.error('查看报修单失败')
  }
}

const cancelRepairOrder = async (id) => {
  try {
    await cancelRepair(id)
    ElMessage.success('取消报修单成功')
    await fetchRepairOrders()
  } catch (error) {
    console.error('Cancel repair order failed:', error)
    ElMessage.error('取消报修单失败')
  }
}

const handleUploadSuccess = (response) => {
  // 假设后端返回 { code: 200, data: '图片url' }
  repairForm.imageUrl = response.data
}

const beforeUpload = (file) => {
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
  }
  return isLt2M
}

onMounted(async () => {
  await fetchRepairOrders()
  await fetchDeviceTypes()
})
</script>

<style scoped>
.repair-order-container {
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