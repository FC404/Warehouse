<template>
    <div class="log-container">
        <el-table :data="logs" style="width: 100%" v-loading="loading" stripe border>
            <el-table-column prop="operateUser" label="操作用户" width="120" />
            <el-table-column prop="operateTime" label="操作时间" width="160" />
            <el-table-column prop="className" label="类名" min-width="400" />
            <el-table-column prop="methodName" label="方法名" width="140" />

            <!-- 方法参数 -->
            <el-table-column label="方法参数" min-width="250">
                <template #default="{ row }">
                    <el-tooltip effect="dark" :content="row.methodParams || ''" placement="top">
                        <span>{{ row.methodParams?.length > 30 ? row.methodParams.slice(0, 30) + '...' :
                            row.methodParams }}</span>
                    </el-tooltip>
                </template>
            </el-table-column>

            <!-- 返回值 -->
            <el-table-column label="返回值" min-width="250">
                <template #default="{ row }">
                    <el-tooltip effect="dark" :content="row.returnValue || ''" placement="top">
                        <span>{{ row.returnValue?.length > 30 ? row.returnValue.slice(0, 30) + '...' : row.returnValue
                            }}</span>
                    </el-tooltip>
                </template>
            </el-table-column>

            <el-table-column prop="costTime" label="耗时(ms)" width="100" />
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container" style="display: flex; justify-content: center; margin-top: 20px;">
            <el-pagination :current-page="pageNum" :page-size="pageSize" :page-sizes="[20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
                @current-change="handlePageChange" />

        </div>

    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getLogList } from '../api'

const logs = ref([])
const loading = ref(false)
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)

async function fetchLogs() {
    loading.value = true
    try {
        const res = await getLogList({ pageNum: pageNum.value, pageSize: pageSize.value })
        logs.value = res.data.data.rows || []
        total.value = res.data.data.total || 0
    } catch (error) {
        console.error('获取日志失败：', error)
    } finally {
        loading.value = false
    }
}

function handlePageChange(newPage) {
  pageNum.value = newPage
  fetchLogs()
}

function handleSizeChange(newSize) {
  pageSize.value = newSize
  pageNum.value = 1
  fetchLogs()
}
onMounted(() => {
    fetchLogs()
})
</script>

<style scoped>
.log-container {
    padding: 30px;
    height:auto;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
    font-family: 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

h1 {
    font-size: 28px;
    font-weight: 700;
    color: #2c3e50;
    margin-bottom: 16px;
    text-align: center;
}

p {
    color: #606266;
    margin-bottom: 24px;
    text-align: center;
}

.el-table {
    border-radius: 12px;
    overflow: hidden;
    font-size: 14px;
}

.el-table th {
    background-color: #ecf0f1;
    color: #2c3e50;
    font-weight: 600;
}

.el-table td {
    color: #34495e;
}

.el-tooltip__popper {
    max-width: 500px;
    white-space: pre-wrap;
    word-break: break-word;
}

.pagination-container {
    margin-top: 20px;
    text-align: right;
}
</style>