<template>
    <div class="orders-container">
        <div class="search-bar">
            <div class="search-bar-header" @click="toggleSearchBar">
                <div class="header-left">
                    <span class="input-icon">
                        <el-icon>
                            <Filter />
                        </el-icon>
                    </span>
                    <h3>搜索筛选</h3>
                </div>
                <div class="toggle-icon">
                    <el-icon>
                        <ArrowDown :class="{ 'rotate-180': isSearchBarExpanded }" />
                    </el-icon>
                </div>
            </div>

            <div v-show="isSearchBarExpanded" class="search-content">
                <div class="search-inputs">
                    <div class="input-wrapper">
                        <label class="input-label">订单号</label>
                        <el-input v-model="filters.orderNumber" placeholder="输入订单号" clearable class="search-input" />
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">客户</label>
                        <el-select v-model="filters.customer" clearable filterable placeholder="请选择客户"
                            class="search-input">
                            <el-option v-for="item in customerOptions" :key="item" :label="item" :value="item" />
                        </el-select>
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">客户料号</label>
                        <el-input v-model="filters.customerPartNumber" placeholder="输入客户料号" clearable
                            class="search-input" />
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">本厂料号</label>
                        <el-input v-model="filters.factoryPartNumber" placeholder="输入本厂料号" clearable
                            class="search-input" />
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">产品名称</label>
                        <el-input v-model="filters.productName" placeholder="输入产品名称" clearable class="search-input" />
                    </div>


                    <div class="input-wrapper">
                        <label class="input-label">状态</label>
                        <el-select v-model="filters.status" placeholder="筛选状态" clearable class="search-input">
                            <el-option label="待处理" value="待处理" />
                            <el-option label="已发货" value="已发货" />
                            <el-option label="已送达" value="已送达" />
                        </el-select>
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">
                            订单日期</label>
                        <el-date-picker v-model="filters.orderDate" type="date" placeholder="选择日期进行查询"
                            format="YYYY-MM-DD" value-format="YYYY-MM-DD" class="search-input" />
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">
                            交付日期</label>
                        <el-date-picker v-model="filters.deliverDate" type="date" placeholder="选择日期进行查询"
                            format="YYYY-MM-DD" value-format="YYYY-MM-DD" class="search-input" />
                    </div>

                </div>

                <div class="buttons-container">
                    <div class="button-group">
                        <el-button type="primary" @click="handleSearch">
                            <el-icon>
                                <Search />
                            </el-icon>搜索
                        </el-button>
                        <el-button @click="handleReset">
                            <el-icon>
                                <RefreshRight />
                            </el-icon>重置
                        </el-button>
                        <el-button type="primary" @click="downloadExcel">导出 Excel</el-button>
                    </div>

                    <div class="button-group">
                        <el-button type="primary" @click="addNewOrder" v-if="userRole === 'admin'">
                            <el-icon>
                                <Plus />
                            </el-icon>添加订单
                        </el-button>
                        <el-button type="danger" @click="batchDelete" v-if="userRole === 'admin'">
                            <el-icon>
                                <Delete />
                            </el-icon>批量删除
                        </el-button>

                    </div>
                </div>
            </div>
        </div>

        <el-table :data="ordersData" border stripe @selection-change="handleSelectionChange" @row-click="handleRowClick"
            v-model:selection="selectedOrders">
            <el-table-column type="selection" width="50" />
            <el-table-column prop="orderNumber" label="订单号" width="90" fixed="left" />
            <el-table-column prop="customer" label="客户" width="100" />
            <el-table-column prop="customerPartNumber" label="客户料号" width="150" show-overflow-tooltip />
            <el-table-column prop="factoryPartNumber" label="本厂料号" width="150" show-overflow-tooltip />
            <el-table-column prop="productName" label="产品名称" width="150" show-overflow-tooltip />
            <el-table-column prop="orderDate" label="订单日期" width="140" />
            <el-table-column prop="deliveryDate" label="交付日期" width="140" />
            <el-table-column prop="orderQuantity" label="订单数量" width="100" />
            <el-table-column prop="deliveredQuantity" label="已交付" width="90" />
            <el-table-column label="欠交数量" width="100">
                <template #default="{ row }">
                    {{ formatQuantityDifference((row.orderQuantity || 0) - (row.deliveredQuantity || 0)) }}
                </template>
            </el-table-column>
            <el-table-column prop="materialProgress" label="投料进度" width="110">
                <template #default="{ row }">
                    <el-tag :type="getProgressTagType(row.materialProgress)">{{ row.materialProgress }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="materialPreparationProgress" label="备料进度" width="110">
                <template #default="{ row }">
                    <el-tag :type="getProgressTagType(row.materialPreparationProgress)">{{
                        row.materialPreparationProgress }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="outsourcing" label="外发进度" width="110">
                <template #default="{ row }">
                    <el-button link @click.stop="showRemarkDialog(row.outsourcing, 'outsourcing', row)">
                        {{ row.outsourcing || '暂无' }}
                    </el-button>
                </template>
            </el-table-column>
            <el-table-column prop="remarks" label="欠料明细" width="110">
                <template #default="{ row }">
                    <el-button link
                        @click.stop="showRemarkDialog(row.materialshortagedetails, 'materialshortagedetails', row)">
                        {{ row.materialshortagedetails || '暂无明细' }}
                    </el-button>
                </template>
            </el-table-column>

            <el-table-column prop="installationProgress" label="安装进度" width="110">
                <template #default="{ row }">
                    <el-tag :type="getProgressTagType(row.installationProgress)">{{ row.installationProgress
                    }}</el-tag>
                </template>
            </el-table-column>

            <!-- 状态列 -->
            <el-table-column prop="status" label="状态" width="110" fixed="right">
                <template #default="{ row }">
                    <el-tag :type="getStatusTagType(row.status)">{{ row.status }}</el-tag>
                </template>
            </el-table-column>

            <el-table-column prop="remarks" label="备注" width="110">
                <template #default="{ row }">
                    <el-button link @click.stop="showRemarkDialog(row.remarks, 'remarks', row)">
                        {{ row.remarks || '暂无备注' }}
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <div style="display: flex; justify-content: center; margin-top: 20px;">
            <el-pagination v-model="currentPage" :page-size="pageSize" :total="totalOrders"
                layout="total, sizes, prev, pager, next, jumper" :page-sizes="[50, 100, 200]"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" class="pagination" />
        </div>
        <!-- 订单编辑弹窗 -->
        <el-dialog v-model="dialogVisible" :title="editingOrder.id ? '编辑订单' : '添加订单'" width="700px"
            :close-on-click-modal="false" :close-on-press-escape="false" v-if="userRole === 'admin'">
            <el-form :model="editingOrder" label-width="120px">
                <!-- 第一行 -->
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="订单号">
                            <el-input v-model="editingOrder.orderNumber" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="客户">
                            <el-input v-model="editingOrder.customer" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <!-- 第二行 -->
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="客户料号">
                            <el-input v-model="editingOrder.customerPartNumber" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="本厂料号">
                            <el-input v-model="editingOrder.factoryPartNumber" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <!-- 第三行 -->
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="产品名称">
                            <el-input v-model="editingOrder.productName" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <!-- 第四行 -->
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="订单日期">
                            <el-date-picker v-model="editingOrder.orderDate" type="date" format="YYYY-MM-DD"
                                value-format="YYYY-MM-DD" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="交付日期">
                            <el-date-picker v-model="editingOrder.deliveryDate" type="date" format="YYYY-MM-DD"
                                value-format="YYYY-MM-DD" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <!-- 第五行 -->
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="订单数量">
                            <el-input-number v-model="editingOrder.orderQuantity" :min="1" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="已交付">
                            <el-input-number v-model="editingOrder.deliveredQuantity" :min="0" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <!-- 第六行 -->
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="投料进度">
                            <el-select v-model="editingOrder.materialProgress">
                                <el-option label="未投料" value="未投料" />
                                <el-option label="已投料" value="已投料" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="备料进度">
                            <el-select v-model="editingOrder.materialPreparationProgress">
                                <el-option label="待备料" value="待备料" />
                                <el-option label="已备料" value="已备料" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <!-- 第七行 -->
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="安装进度">
                            <el-select v-model="editingOrder.installationProgress">
                                <el-option label="待上线" value="待上线" />
                                <el-option label="已上线" value="已上线" />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="外发进度">
                            <el-input v-model="editingOrder.outsourcing" type="textarea" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="欠料明细">
                            <el-input v-model="editingOrder.materialshortagedetails" type="textarea" />
                        </el-form-item>

                        <el-form-item label="备注">
                            <el-input v-model="editingOrder.remarks" type="textarea" />
                        </el-form-item>
                        <el-form-item>
                            <template #label>
                                <span class="required-label">状态</span>
                            </template>
                            <el-select v-model="editingOrder.status">
                                <el-option label="待处理" value="待处理" />
                                <el-option label="已发货" value="已发货" />
                                <el-option label="已送达" value="已送达" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <el-button type="danger" v-if="editingOrder.id" @click="deleteOrder">删除</el-button>
                <el-button type="primary" @click="saveOrderChanges">保存</el-button>
                <el-button @click="dialogVisible = false">取消</el-button>
            </template>
        </el-dialog>

        <!-- 在最后一个dialog后添加备注查看弹窗 -->
        <el-dialog v-model="remarkDialogVisible" title="修改/查看" width="30%" :show-close="true"
            :close-on-click-modal="false" :close-on-press-escape="false" @click.stop>
            <el-input v-model="editingRemark.content" type="textarea" :rows="3" resize="both"
                :disabled="userRole !== 'admin'" />
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="submitRemarkEdit">保存</el-button>
                    <el-button @click="remarkDialogVisible = false">取消</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrders, createOrder, batchDeleteOrders, updateOrder, deleteOrderById, exportOrders, validateRole, getOrderCustomers } from '../api'
import { Search, RefreshRight, Plus, Delete, Filter, ArrowDown } from '@element-plus/icons-vue'
const ordersData = ref([]) // 订单数据
const currentPage = ref(1) // 当前页
const pageSize = ref(50) // 每页显示数量
const selectedOrders = ref([]) // 选中的订单
const totalOrders = ref(0) // 总订单数
const dialogVisible = ref(false) // 弹窗显示状态
const editingOrder = ref({}) // 正在编辑的订单
const customerOptions = ref([]) // 客户下拉选项

const remarkDialogVisible = ref(false)
const editingRemark = ref({
    content: '',
    type: '',
    row: null
})
const isSearchBarExpanded = ref(true) // 搜索栏展开状态，默认展开

const downloadExcel = async () => {
    const ids = selectedOrders.value.map(order => order.id);
    if (ids.length === 0) {
        ElMessage.warning('请先选择要导出的记录');
        return;
    }

    try {
        const response = await exportOrders(ids);
        const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
        const url = window.URL.createObjectURL(blob);

        // 创建下载链接
        const link = document.createElement('a');
        link.href = url;
        link.download = '订单.xlsx'; // 下载的文件名
        link.click();

        // 释放 URL
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error("导出订单失败:", error);
        alert("导出失败，请稍后重试！");
    }
};
// 格式化欠交数量显示
const formatQuantityDifference = (difference) => {
    if (difference > 0) {
        return `-${difference}`  // 欠交显示负号
    } else if (difference < 0) {
        return `+${Math.abs(difference)}`  // 多交显示加号
    }
    return '0'  // 刚好相等
}
// 搜索筛选条件
const filters = ref({
    orderNumber: '',
    customer: '',
    customerPartNumber: '',
    factoryPartNumber: '',
    productName: '',
    orderDate: null,
    deliverDate: null,
    status: ''
})

// 切换搜索栏展开/折叠状态
const toggleSearchBar = () => {
    isSearchBarExpanded.value = !isSearchBarExpanded.value
}

// 从后端加载订单数据
const loadOrders = async () => {
    try {
        const response = await getOrders({
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            orderNumber: filters.value.orderNumber || null,
            customer: filters.value.customer || null,
            customerPartNumber: filters.value.customerPartNumber || null,
            factoryPartNumber: filters.value.factoryPartNumber || null,
            productName: filters.value.productName || null,
            status: filters.value.status || null,
            orderDate: filters.value.orderDate || null,
            deliverDate: filters.value.deliverDate || null
        })
        ordersData.value = response.data.data.rows
        totalOrders.value = response.data.data.total
    } catch (error) {
        console.error('获取订单失败:', error)
        ElMessage.error('无法加载订单数据')
    }
}
// 获取本地存储的 token
const getToken = localStorage.getItem('token')
const userRole = ref('')

onMounted(async () => {
    loadOrders()
    loadCustomerOptions()
    const res = await validateRole(getToken)
    if (res.data.code === 0) {
        userRole.value = res.data.data // "admin" 或 "user"
    }

})

// 搜索订单
const handleSearch = () => {
    currentPage.value = 1
    loadOrders()
}

// 分页处理
const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
    loadOrders()
}

const handleCurrentChange = (val) => {
    currentPage.value = val
    loadOrders()
}

// 选中订单
const handleSelectionChange = (val) => {
    selectedOrders.value = val
}
// 添加新订单
const addNewOrder = () => {
    editingOrder.value = {
        orderNumber: '',
        customer: '',
        customerPartNumber: '',
        factoryPartNumber: '',
        orderDate: '',
        deliveryDate: '',
        orderQuantity: 1,
        deliveredQuantity: 0,
        status: '待处理',
        productName: '',  // 新增字段：产品名称
        materialProgress: '未投料',  // 新增字段：投料进度
        materialPreparationProgress: '待备料',  // 新增字段：备料进度
        materialshortagedetailsL: '',
        installationProgress: '待上线',  // 新增字段：安装进度
        remarks: '',
        outsourcing: ""
    }
    dialogVisible.value = true
    // 重新加载数据
    loadCustomerOptions()
}

function toLocalDateString(val) {
    if (!val) return '';

    // 如果已经是 YYYY-MM-DD 字符串，直接返回
    if (typeof val === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(val)) return val;

    // 如果是 ISO 字符串，先 new Date，再加上时区偏移
    if (typeof val === 'string' && val.includes('T')) {
        const d = new Date(val);
        if (!isNaN(d)) {
            const local = new Date(d.getTime() + d.getTimezoneOffset() * 60000); // 加上本地偏移
            return `${local.getFullYear()}-${String(local.getMonth() + 1).padStart(2, '0')}-${String(local.getDate()).padStart(2, '0')}`;
        }
    }

    // 如果是 Date 对象
    if (val instanceof Date) {
        return `${val.getFullYear()}-${String(val.getMonth() + 1).padStart(2, '0')}-${String(val.getDate()).padStart(2, '0')}`;
    }

    return '';
}

// 编辑订单
const handleRowClick = (row) => {
    editingOrder.value = {
        ...row,
        orderDate: toLocalDateString(row.orderDate),
        deliveryDate: toLocalDateString(row.deliveryDate),
        productName: row.productName || '',  // 初始化产品名称
        materialProgress: row.materialProgress || '未投料',  // 初始化投料进度
        materialPreparationProgress: row.materialPreparationProgress || '待备料',  // 初始化备料进度
        installationProgress: row.installationProgress || '待上线',  // 初始化安装进度
        outsourcing: row.outsourcing || '待外发'  // 初始化外发进度
    }
    dialogVisible.value = true
}


// 保存订单
const saveOrderChanges = async () => {
    console.log('editingOrder.orderDate:', editingOrder.value.orderDate, typeof editingOrder.value.orderDate);

    editingOrder.value.orderDate = toLocalDateString(editingOrder.value.orderDate);
    editingOrder.value.deliveryDate = toLocalDateString(editingOrder.value.deliveryDate);


    try {
        if (!editingOrder.value.id) {
            await createOrder(editingOrder.value);
            ElMessage.success('订单添加成功');
        } else {
            await updateOrder(editingOrder.value.id, editingOrder.value);
            ElMessage.success('订单更新成功');
        }
        dialogVisible.value = false;
        loadOrders();
        loadCustomerOptions();
    } catch (error) {
        console.error('操作失败:', error);
        ElMessage.error('操作失败');
    }
};

// 批量删除订单（带确认弹框）
const batchDelete = async () => {
    if (selectedOrders.value.length === 0) {
        ElMessage.warning('请先选择要删除的记录');
        return;
    }

    try {
        await ElMessageBox.confirm('此操作将永久删除选中的订单，是否继续？', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });

        const ids = selectedOrders.value.map(order => order.id);
        await batchDeleteOrders(ids);
        ElMessage.success('批量删除成功');
        // 重新加载数据
        loadOrders()
        loadCustomerOptions()
    } catch (error) {
        if (error === 'cancel') {
            ElMessage.info('已取消删除');
        } else {
            console.error('删除失败:', error);
            ElMessage.error('删除失败，请稍后重试');
        }
    }
};

// 状态标签样式
const getStatusTagType = (status) => {
    return {
        '已送达': 'success',
        '已发货': 'warning',
        '待处理': 'danger'
    }[status] || ''
}

// 根据进度状态返回标签颜色（已完成绿色，未完成红色）
const getProgressTagType = (progress) => {
    if (progress === '已投料' || progress === '已备料' || progress === '已上线' || progress === '已外发') {
        return 'success';  // 绿色标签，表示已完成
    }
    return 'danger';  // 红色标签，表示未完成
}




// 删除单个订单（带确认弹框）
const deleteOrder = async () => {
    try {
        await ElMessageBox.confirm('此操作将永久删除该订单，是否继续？', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        });

        await deleteOrderById(editingOrder.value.id);
        ElMessage.success('订单删除成功');
        dialogVisible.value = false;
        loadOrders();
    } catch (error) {
        console.log('取消删除', error);
    }
};

// 显示备注弹窗
const showRemarkDialog = (content, type, row) => {
    editingRemark.value = {
        content: content || '',
        type: type,
        row: row
    }
    remarkDialogVisible.value = true
}

// 提交备注修改
const submitRemarkEdit = async () => {
    try {
        const updateData = {
            ...editingRemark.value.row,
            [editingRemark.value.type]: editingRemark.value.content
        }

        await updateOrder(editingRemark.value.row.id, updateData)
        ElMessage.success('修改成功')
        remarkDialogVisible.value = false
        loadOrders() // 刷新订单列表
    } catch (error) {
        console.error('修改失败:', error)
        ElMessage.error('修改失败，请稍后重试')
    }
}


const loadCustomerOptions = async () => {
    try {
        const res = await getOrderCustomers()
        customerOptions.value = res.data.data
    } catch (error) {
        console.error('获取客户列表失败', error)
    }
}


const handleReset = () => {
    // 重置所有搜索条件
    filters.value = {
        orderNumber: '',
        customer: '',
        customerPartNumber: '',
        factoryPartNumber: '',
        productName: '',
        orderDate: null,
        deliverDate: null,
        status: ''
    }

    // 重置页码
    currentPage.value = 1

    // 重新加载数据
    loadOrders()
}
</script>

<style>
/* Main container styles */
.orders-container {
    padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}



/* Search bar container */
.search-bar {
    background-color: #f9fafb;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    margin-bottom: 20px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* Search bar header */
.search-bar-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px;
    cursor: pointer;
    user-select: none;
}

.search-bar-header:hover {
    background-color: #f3f4f6;
    border-radius: 8px 8px 0 0;
}

.header-left {
    display: flex;
    align-items: center;
}

.search-bar-header h3 {
    font-size: 14px;
    font-weight: 600;
    color: #4b5563;
    margin: 0;
    margin-left: 8px;
}

/* Toggle icon */
.toggle-icon {
    color: #6b7280;
}

.rotate-180 {
    transform: rotate(180deg);
}

/* Search content */
.search-content {
    padding: 16px;
    border-top: 1px solid #e5e7eb;
}

/* Search inputs container */
.search-inputs {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-bottom: 16px;
}

.required-label {
    color: #f56c6c;
    /* Element Plus 默认的红色 */
}

/* Individual input wrapper */
.input-wrapper {
    flex: 1;
    margin-bottom: 12px;
}

/* Input label */
.input-label {
    display: block;
    font-size: 14px;
    font-weight: 500;
    color: #4b5563;
    margin-bottom: 6px;
}

/* Style for all inputs */
.search-input {
    width: 100%;
}

/* Buttons container */
.buttons-container {
    display: flex;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 12px;
    padding-top: 8px;
    border-top: 1px solid #e5e7eb;
}

.button-group {
    display: flex;
    gap: 8px;
}

/* Pagination */
.pagination {
    margin-top: 20px;
    text-align: right;
}

/* Responsive adjustments */
@media (max-width: 768px) {
    .search-inputs {
        flex-direction: column;
    }

    .input-wrapper {
        width: 100%;
    }

    .buttons-container {
        flex-direction: column;
    }

    .button-group {
        width: 100%;
        justify-content: flex-end;
    }
}
</style>
