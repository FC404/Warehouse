<template>
  <div class="task-management-container">
    <div class="main-content">
      <!-- 顶部操作栏 -->
      <div class="top-bar">
        <div class="search-section">
          <el-input 
            v-model="filters.taskname" 
            placeholder="搜索任务..." 
            clearable 
            class="search-input"
            :prefix-icon="Search"
            @input="handleSearch"
          />
          <el-select 
            v-model="filters.status" 
            placeholder="状态筛选" 
            clearable 
            class="status-filter"
            @change="handleSearch"
          >
            <el-option label="全部" value="" />
            <el-option label="完成" value="完成" />
            <el-option label="未完成" value="未完成" />
          </el-select>
        </div>
        
        <div class="action-section">
          <el-button type="primary" @click="openAddDialog" :icon="Plus" class="action-btn">
            新建任务
          </el-button>
          <el-button 
            type="primary" 
            @click="deleteSelectedTasks" 
            :icon="Delete" 
            class="action-btn"
            :disabled="selectedTasks.length === 0"
            plain
          >
            删除选中 ({{ selectedTasks.length }})
          </el-button>
        </div>
      </div>

      <!-- 任务统计 -->
      <div class="stats-bar">
        <div class="stat-item">
          <span class="stat-number">{{ taskList.length }}</span>
          <span class="stat-label">总任务</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ completedCount }}</span>
          <span class="stat-label">已完成</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ pendingCount }}</span>
          <span class="stat-label">待完成</span>
        </div>
      </div>

      <!-- 任务列表 -->
      <div class="task-list-wrapper">
        <div 
          v-infinite-scroll="loadMore" 
          class="task-list-container"
          :infinite-scroll-disabled="loading || noMore"
          :infinite-scroll-distance="10"
        >
          <div 
            v-for="task in taskList" 
            :key="task.id" 
            class="task-card"
            :class="{ 'completed': task.status === '完成' }"
          >
            <div class="task-checkbox">
              <el-checkbox 
                :model-value="selectedTasks.some(t => t.id === task.id)"
                @change="handleTaskSelection(task, $event)"
                size="large"
              />
            </div>
            
            <div class="task-main">
              <div class="task-content">
                <h3 class="task-title">{{ task.taskname }}</h3>
                <div class="task-meta">
                  <el-tag 
                    :type="task.status === '完成' ? 'success' : ''" 
                    class="status-tag"
                    size="small"
                  >
                    {{ task.status }}
                  </el-tag>
                </div>
              </div>
              
              <div class="task-actions">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="openEditDialog(task)"
                  :icon="Edit"
                  text
                  class="task-btn"
                >
                  编辑
                </el-button>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="deleteItemTask(task)"
                  :icon="Delete"
                  text
                  class="task-btn delete-btn"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
          
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-state">
            <el-icon class="is-loading loading-icon"><Loading /></el-icon>
            <span>加载中...</span>
          </div>
          
          <!-- 没有更多数据 -->
          <div v-if="noMore && taskList.length > 0" class="no-more-state">
            <el-icon><Check /></el-icon>
            <span>已加载全部任务</span>
          </div>
          
          <!-- 空状态 -->
          <div v-if="taskList.length === 0 && !loading" class="empty-state">
            <el-icon class="empty-icon"><DocumentAdd /></el-icon>
            <h3>还没有任务</h3>
            <p>点击"新建任务"开始添加您的第一个任务</p>
            <el-button type="primary" @click="openAddDialog" :icon="Plus" class="empty-action-btn">
              新建任务
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 弹窗 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isAddMode ? '新建任务' : '编辑任务'" 
      width="500px"
      class="task-dialog"
    >
      <el-form :model="currentTask" label-width="0" class="task-form">
        <div class="form-group">
          <label class="form-label">任务内容</label>
          <el-input 
            v-model="currentTask.taskname" 
            type="textarea" 
            :rows="4"
            placeholder="请输入任务内容..."
            class="form-textarea"
          />
        </div>
        <div class="form-group">
          <label class="form-label">任务状态</label>
          <el-select v-model="currentTask.status" class="form-select">
            <el-option label="未完成" value="未完成" />
            <el-option label="完成" value="完成" />
          </el-select>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" class="cancel-btn">取消</el-button>
          <el-button type="primary" @click="saveTask" class="save-btn">
            {{ isAddMode ? '创建任务' : '保存修改' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Plus,
  Delete,
  Edit,
  Loading,
  Check,
  DocumentAdd
} from '@element-plus/icons-vue'

import { getTaskbarData, updateTask, deleteTasks, addTask } from '../api.js'

// 状态数据
const taskList = ref([])
const selectedTasks = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)
const loading = ref(false)
const noMore = ref(false)

const filters = reactive({ taskname: '', status: '' })

const dialogVisible = ref(false)
const isAddMode = ref(false)
const currentTask = ref({ id: null, taskname: '', status: '未完成' })

// 计算属性
const completedCount = computed(() => 
  taskList.value.filter(task => task.status === '完成').length
)

const pendingCount = computed(() => 
  taskList.value.filter(task => task.status === '未完成').length
)

// 任务选择处理
const handleTaskSelection = (task, checked) => {
  if (checked) {
    selectedTasks.value.push(task)
  } else {
    selectedTasks.value = selectedTasks.value.filter(t => t.id !== task.id)
  }
}

const openEditDialog = (task) => {
  currentTask.value = { ...task }
  isAddMode.value = false
  dialogVisible.value = true
}

// 获取任务数据
const fetchTasks = async (isLoadMore = false) => {
  if (loading.value) return
  
  loading.value = true
  try {
    const res = await getTaskbarData({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      taskname: filters.taskname || undefined,
      status: filters.status || undefined
    })
    
    if (res.data.code === 0) {
      const newTasks = res.data.data.rows
      
      if (isLoadMore) {
        taskList.value = [...taskList.value, ...newTasks]
      } else {
        taskList.value = newTasks
      }
      
      total.value = res.data.data.total
      
      if (taskList.value.length >= total.value) {
        noMore.value = true
      } else {
        noMore.value = false
      }
    } else {
      ElMessage.error('获取任务失败')
    }
  } catch (e) {
    ElMessage.error('请求失败，请检查网络')
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (loading.value || noMore.value) return
  pageNum.value++
  fetchTasks(true)
}

const handleSearch = () => {
  pageNum.value = 1
  noMore.value = false
  selectedTasks.value = []
  fetchTasks()
}

const openAddDialog = () => {
  currentTask.value = { id: null, taskname: '', status: '未完成' }
  isAddMode.value = true
  dialogVisible.value = true
}

const saveTask = async () => {
  if (!currentTask.value.taskname.trim()) {
    ElMessage.warning('请输入任务内容')
    return
  }
  try {
    if (isAddMode.value) {
      await addTask(currentTask.value)
      ElMessage.success('任务创建成功')
    } else {
      await updateTask(currentTask.value)
      ElMessage.success('任务更新成功')
    }
    dialogVisible.value = false
    handleSearch()
  } catch (err) {
    ElMessage.error(isAddMode.value ? '创建失败' : '更新失败')
  }
}

const deleteItemTask = async (task) => {
  try {
    await ElMessageBox.confirm('确定要删除这个任务吗？', '确认删除', { 
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deleteTasks([task.id])
    ElMessage.success('删除成功')
    handleSearch()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const deleteSelectedTasks = async () => {
  const idsToDelete = selectedTasks.value.map(t => t.id)

  if (idsToDelete.length === 0) {
    ElMessage.warning('请先选择要删除的任务')
    return
  }

  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${idsToDelete.length} 个任务吗？`, '确认删除', { 
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
    await deleteTasks(idsToDelete)
    ElMessage.success('删除成功')
    handleSearch()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  fetchTasks()
})
</script>

<style scoped>
.task-management-container {
  min-height: 100vh;
  padding: 0;
}

.page-header {
  background: white;
  padding: 32px 24px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
}

.main-content {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 20px;
}

.search-section {
  display: flex;
  gap: 12px;
  flex: 1;
  max-width: 500px;
}

.search-input {
  flex: 1;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.status-filter {
  width: 140px;
}

.status-filter :deep(.el-select__wrapper) {
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.action-section {
  display: flex;
  gap: 12px;
}

.action-btn {
  border-radius: 8px;
  font-weight: 500;
  padding: 10px 20px;
}

.stats-bar {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.task-list-wrapper {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
  overflow: hidden;
}

.task-list-container {
  max-height: 600px;
  overflow-y: auto;
}

.task-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f3f4f6;
  transition: all 0.2s;
}

.task-card:hover {
  background: #f9fafb;
}

.task-card.completed {
  opacity: 0.7;
}

.task-card.completed .task-title {
  text-decoration: line-through;
  color: #6b7280;
}

.task-checkbox {
  margin-right: 16px;
}

.task-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex: 1;
}

.task-content {
  flex: 1;
}

.task-title {
  font-size: 16px;
  font-weight: 500;
  color: #1f2937;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-tag {
  border-radius: 6px;
}

.task-actions {
  display: flex;
  gap: 8px;
}

.task-btn {
  border-radius: 6px;
  font-weight: 500;
}

.delete-btn:hover {
  color: #ef4444 !important;
}

.loading-state,
.no-more-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px;
  color: #6b7280;
  gap: 8px;
}

.loading-icon {
  font-size: 18px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #6b7280;
}

.empty-icon {
  font-size: 48px;
  color: #d1d5db;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  color: #374151;
  margin: 0 0 8px 0;
}

.empty-state p {
  margin: 0 0 24px 0;
  font-size: 14px;
}

.empty-action-btn {
  border-radius: 8px;
}

/* 弹窗样式 */
.task-dialog :deep(.el-dialog) {
  border-radius: 12px;
}

.task-dialog :deep(.el-dialog__header) {
  padding: 24px 24px 0;
}

.task-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.task-form {
  padding: 0 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 8px;
}

.form-textarea :deep(.el-textarea__inner) {
  border-radius: 8px;
  border: 1px solid #d1d5db;
  font-family: inherit;
}

.form-select {
  width: 100%;
}

.form-select :deep(.el-select__wrapper) {
  border-radius: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px 24px;
}

.cancel-btn,
.save-btn {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
}

.cancel-btn {
  color: #6b7280;
  border-color: #d1d5db;
}

/* 响应式 */
@media (max-width: 768px) {
  .main-content {
    padding: 16px;
  }
  
  .top-bar {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .search-section {
    max-width: none;
  }
  
  .action-section {
    justify-content: stretch;
  }
  
  .action-btn {
    flex: 1;
  }
  
  .stats-bar {
    justify-content: space-around;
  }
  
  .task-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .task-main {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .task-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .task-dialog :deep(.el-dialog) {
    width: 90% !important;
    margin: 5vh auto;
  }
}
</style>