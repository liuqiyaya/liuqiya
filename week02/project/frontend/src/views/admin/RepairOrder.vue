<template>
  <div class="repair-order-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报修管理</span>
          <!-- 添加筛选下拉框 -->
          <div class="filter-section">
            <el-select 
              v-model="filterStatus" 
              placeholder="按状态筛选" 
              clearable
              @change="handleFilterChange"
              style="width: 120px"
            >
              <el-option label="全部" value=""></el-option>
              <el-option label="待处理" value="待处理"></el-option>
              <el-option label="处理中" value="处理中"></el-option>
              <el-option label="已完成" value="已完成"></el-option>
              <el-option label="已取消" value="已取消"></el-option>
            </el-select>
            <el-button @click="fetchRepairOrders" style="margin-left: 10px">刷新</el-button>
          </div>
        </div>
      </template>
      <el-table :data="repairOrders" style="width: 100%">
        <el-table-column prop="id" label="报修单号" width="100" />
        <el-table-column prop="studentId" label="学生ID" width="80" />
        <el-table-column prop="dormBuilding" label="宿舍楼" width="80" />
        <el-table-column prop="dormRoom" label="宿舍号" width="80" />
        <el-table-column prop="problemDescription" label="问题描述" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        
        <!-- 操作列 -->
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewRepairOrder(scope.row.id)">
              查看
            </el-button>
            <el-button
              v-if="scope.row.status !== '已完成' && scope.row.status !== '已取消'"
              type="success"
              size="small"
              @click="showUpdateDialog(scope.row)"
            >
              修改状态
            </el-button>
            <el-button
              v-if="scope.row.status === '待处理'"
              type="danger"
              size="small"
              @click="deleteRepairOrder(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 查看报修单对话框 -->
    <el-dialog v-model="showViewDialog" title="报修单详情" width="500px">
      <el-form :model="currentRepairOrder" label-width="100px">
        <el-form-item label="报修单号">
          <el-input v-model="currentRepairOrder.id" disabled></el-input>
        </el-form-item>
        <el-form-item label="学生ID">
          <el-input v-model="currentRepairOrder.studentId" disabled></el-input>
        </el-form-item>
        <el-form-item label="宿舍楼">
          <el-input v-model="currentRepairOrder.dormBuilding" disabled></el-input>
        </el-form-item>
        <el-form-item label="宿舍号">
          <el-input v-model="currentRepairOrder.dormRoom" disabled></el-input>
        </el-form-item>
        <el-form-item label="设备类型">
          <el-input v-model="currentRepairOrder.deviceTypeId" disabled></el-input>
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input v-model="currentRepairOrder.problemDescription" type="textarea" rows="3" disabled></el-input>
        </el-form-item>
        
        <!-- 图片显示 -->
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

    <!-- 修改状态对话框 -->
    <el-dialog v-model="showUpdateDialogFlag" title="修改状态" width="400px">
      <el-form :model="updateForm" :rules="updateRules" ref="updateFormRef" label-width="80px">
        <el-form-item label="状态" prop="status">
          <el-select v-model="updateForm.status" placeholder="请选择状态">
            <el-option label="待处理" value="待处理"></el-option>
            <el-option label="处理中" value="处理中"></el-option>
            <el-option label="已完成" value="已完成"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showUpdateDialogFlag = false">取消</el-button>
          <el-button type="primary" @click="updateStatus">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getAllRepairOrders,
  getRepairOrdersByStatus,  // 新增：按状态查询
  getAdminRepairOrderDetail,
  updateRepairOrderStatus,
  deleteRepairOrder as deleteRepair
} from '../../services/api'

const repairOrders = ref([])
const showViewDialog = ref(false)
const showUpdateDialogFlag = ref(false)
const updateFormRef = ref(null)
const currentRepairOrderId = ref(null)

// 筛选状态
const filterStatus = ref('')

const currentRepairOrder = reactive({
  id: '',
  studentId: '',
  dormBuilding: '',
  dormRoom: '',
  deviceTypeId: '',
  problemDescription: '',
  imageUrl: '',
  status: '',
  createTime: ''
})

const updateForm = reactive({
  status: ''
})

const updateRules = {
  status: [
    { required: true, message: '请选择状态', trigger: 'blur' }
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

// 获取报修单（支持筛选）
const fetchRepairOrders = async () => {
  try {
    let response
    if (filterStatus.value) {
      // 有筛选条件，调用按状态查询接口
      response = await getRepairOrdersByStatus(filterStatus.value)
    } else {
      // 无筛选条件，查询全部
      response = await getAllRepairOrders()
    }
    repairOrders.value = response.data.data
  } catch (error) {
    console.error('Fetch repair orders failed:', error)
    ElMessage.error('获取报修单失败')
  }
}

// 筛选条件变化时重新查询
const handleFilterChange = () => {
  fetchRepairOrders()
}

const viewRepairOrder = async (id) => {
  try {
    const response = await getAdminRepairOrderDetail(id)
    Object.assign(currentRepairOrder, response.data.data)
    showViewDialog.value = true
  } catch (error) {
    console.error('View repair order failed:', error)
    ElMessage.error('查看报修单失败')
  }
}

const showUpdateDialog = (row) => {
  currentRepairOrderId.value = row.id
  updateForm.status = row.status
  showUpdateDialogFlag.value = true
}

const updateStatus = async () => {
  if (!updateFormRef.value) return
  await updateFormRef.value.validate(async (valid) => {
    if (valid && currentRepairOrderId.value) {
      try {
        await updateRepairOrderStatus(currentRepairOrderId.value, updateForm.status)
        ElMessage.success('修改状态成功')
        showUpdateDialogFlag.value = false
        await fetchRepairOrders()
      } catch (error) {
        console.error('Update status failed:', error)
        ElMessage.error('修改状态失败')
      }
    }
  })
}

const deleteRepairOrder = async (id) => {
  try {
    await deleteRepair(id)
    ElMessage.success('删除报修单成功')
    await fetchRepairOrders()
  } catch (error) {
    console.error('Delete repair order failed:', error)
    ElMessage.error('删除报修单失败')
  }
}

onMounted(async () => {
  await fetchRepairOrders()
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

.filter-section {
  display: flex;
  align-items: center;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>