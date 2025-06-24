<template>
    <div class="machine-plan-container">
        <!-- 可折叠的搜索栏 -->
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
                        <label class="input-label">客户</label>
                        <el-select v-model="filters.customer" placeholder="请选择客户" clearable filterable>
                            <el-option v-for="item in materialFormCustomers" :key="item" :label="item" :value="item" />
                        </el-select>
                    </div>
                    <div class="input-wrapper">
                        <label class="input-label">
                            母件料号</label>
                        <el-input v-model="filters.parentPartNumber" placeholder="请输入母件料号" clearable>
                            <template #prefix>
                                <el-icon>
                                    <Search />
                                </el-icon>
                            </template>
                        </el-input>
                    </div>
                    <div class="input-wrapper">
                        <label class="input-label">铜材料号</label>
                        <el-input v-model="filters.copperMaterialNumber" placeholder="请输入铜材料号" clearable>
                            <template #prefix>
                                <el-icon>
                                    <Search />
                                </el-icon>
                            </template>
                        </el-input>
                    </div>
                    <div class="input-wrapper">
                        <label class="input-label">子件料号</label>
                        <el-input v-model="filters.childPartNumber" placeholder="请输入子件料号" clearable>
                            <template #prefix>
                                <el-icon>
                                    <Search />
                                </el-icon>
                            </template>
                        </el-input>
                    </div>
                    <div class="input-wrapper">
                        <label class="input-label">材料规格</label>
                        <el-input v-model="filters.materialSpecification" placeholder="请输入材料规格" clearable>
                            <template #prefix>
                                <el-icon>
                                    <Search />
                                </el-icon>
                            </template>
                        </el-input>
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">材质</label>
                        <el-select v-model="filters.materialType" placeholder="选择材质" clearable>
                            <el-option label="非标" value="非标"></el-option>
                            <el-option label="国标" value="国标"></el-option>
                            <el-option label="无铅" value="无铅"></el-option>
                            <el-option label="617" value="617"></el-option>
                            <el-option label="304" value="304"></el-option>
                            <el-option label="316" value="316"></el-option>
                            <el-option label="低铅" value="低铅"></el-option>
                            <el-option label="塑料" value="塑料"></el-option>
                        </el-select>
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">
                            投料日期</label>
                        <el-date-picker v-model="filters.feedingDate" type="date" placeholder="选择日期进行查询"
                            format="YYYY-MM-DD" value-format="YYYY-MM-DD" class="w-full" />
                    </div>
                    <div class="input-wrapper">
                        <label class="input-label">
                            机加交期</label>
                        <el-date-picker v-model="filters.machiningDeliveryDate" type="date" placeholder="选择日期进行查询"
                            format="YYYY-MM-DD" value-format="YYYY-MM-DD" class="w-full" />
                    </div>
                    <div class="input-wrapper">
                        <label class="input-label">自制/外发</label>
                        <el-select v-model="filters.manufacturingType" placeholder="选择自制/外发" clearable>
                            <el-option label="自制" value="自制"></el-option>
                            <el-option label="外发" value="外发"></el-option>
                        </el-select>
                    </div>
                    <div class="input-wrapper">
                        <label class="input-label">下单日期</label>
                        <el-date-picker v-model="filters.orderDate" type="date" placeholder="选择日期进行查询"
                            format="YYYY-MM-DD" value-format="YYYY-MM-DD" class="w-full" />
                    </div>
                    <div class="input-wrapper">
                        <label class="input-label">
                            上线日期</label>
                        <el-date-picker v-model="filters.onlineDate" type="date" placeholder="选择日期进行查询"
                            format="YYYY-MM-DD" value-format="YYYY-MM-DD" class="w-full" />
                    </div>
                    <div class="input-wrapper">
                        <label class="input-label">订单状态</label>
                        <el-select v-model="filters.orderStatus" placeholder="选择订单状态" clearable>
                            <el-option label="未完成" value="未完成"></el-option>
                            <el-option label="完成" value="完成"></el-option>
                            <el-option label="异常" value="异常"></el-option>
                        </el-select>
                    </div>
                </div>

                <div class="buttons-container">
                    <div class="button-group">
                        <el-button type="primary" @click="handleSearch">
                            <el-icon>
                                <Search />
                            </el-icon>查询
                        </el-button>
                        <el-button @click="handleReset">
                            <el-icon>
                                <Refresh />
                            </el-icon>重置
                        </el-button>
                        <el-button type="primary" @click="downloadExcel">导出 Excel</el-button>
                    </div>

                    <div class="button-group">
                        <el-button type="primary" @click="handleAddOrder" v-if="userRole === 'admin'">
                            <el-icon>
                                <Plus />
                            </el-icon>添加订单
                        </el-button>
                        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0"
                            v-if="userRole === 'admin'">
                            <el-icon>
                                <Delete />
                            </el-icon>批量删除
                        </el-button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 物料表格 -->
        <el-table :data="materialForms" style="width: 100%" stripe border @row-click="handleRowClick"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" />
            <el-table-column prop="customer" label="客户" width="90"></el-table-column>
            <el-table-column prop="parentPartNumber" label="母件料号" width="120"></el-table-column>
            <el-table-column prop="copperMaterialNumber" label="铜材料号" width="120"></el-table-column>
            <el-table-column prop="childPartNumber" label="子件料号" width="120"></el-table-column>
            <el-table-column prop="materialSpecification" label="材料规格" width="450"></el-table-column>
            <el-table-column prop="materialType" label="材质" width="120"></el-table-column>
            <el-table-column prop="quantity" label="用量" width="120"></el-table-column>
            <el-table-column prop="copperQuantity" label="铜材数量" width="120"></el-table-column>
            <el-table-column prop="orderQuantity" label="订单数量" width="120"></el-table-column>
            <el-table-column prop="plannedQuantity" label="计划数量" width="120"></el-table-column>
            <el-table-column prop="feedingDate" label="投料日期" width="180"></el-table-column>
            <el-table-column prop="machiningDeliveryDate" label="机加交期" width="180"></el-table-column>
            <el-table-column label="自制/外发" width="180">
                <template #default="{ row }">
                    <el-tag :type="row.manufacturingType === '自制' ? 'success' : 'danger'" disable-transitions>
                        {{ row.manufacturingType }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="orderDate" label="下单日期" width="180"></el-table-column>
            <el-table-column prop="onlineDate" label="上线日期" width="180"></el-table-column>
            <el-table-column label="订单状态" width="180" fixed="right">
                <template #default="{ row }">
                    <el-tag :type="getOrderStatusTagType(row.orderStatus)" disable-transitions>
                        {{ row.orderStatus }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="备注" width="180">
                <template #default="{ row }">
                    <div class="remark-text" @click.stop="showRemarkDialog(row)" style="cursor: pointer;">
                        {{ row.remarks || '点击查看备注' }}
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <div style="display: flex; justify-content: center; margin-top: 20px;">
            <el-pagination v-model="currentPage" :page-size="pageSize" :total="totalItems"
                layout="total, sizes, prev, pager, next, jumper" :page-sizes="[20, 50, 100]"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" class="pagination" />
        </div>
        <!-- 订单详情弹窗 -->
        <el-dialog v-model="dialogVisible" :title="isEditMode ? '编辑订单' : '添加订单'" width="70%"
            :close-on-click-modal="false" :close-on-press-escape="false" v-if="userRole === 'admin'">
            <el-form :model="selectedOrder" label-width="120px" class="order-form">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="客户" >
                            <template #label>
                                <span class="required-label">客户</span>
                            </template>
                            <el-input v-model="selectedOrder.customer"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="母件料号">
                            <el-input v-model="selectedOrder.parentPartNumber"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="铜材料号">
                            <el-input v-model="selectedOrder.copperMaterialNumber"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="子件料号">
                            <el-input v-model="selectedOrder.childPartNumber"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="材料规格">
                            <el-input v-model="selectedOrder.materialSpecification"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="材质">
                            <template #label>
                                <span class="required-label">材质</span>
                            </template>
                            <el-select v-model="selectedOrder.materialType" placeholder="请选择材质">
                                <el-option label="非标" value="非标"></el-option>
                                <el-option label="国标" value="国标"></el-option>
                                <el-option label="无铅" value="无铅"></el-option>
                                <el-option label="617" value="617"></el-option>
                                <el-option label="304" value="304"></el-option>
                                <el-option label="316" value="316"></el-option>
                                <el-option label="低铅" value="低铅"></el-option>
                                <el-option label="塑料" value="塑料"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="用量">
                            <el-input-number v-model="selectedOrder.quantity" :min="0" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="铜材数量">
                            <el-input-number v-model="selectedOrder.copperQuantity" :min="0" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="订单数量">
                            <el-input-number v-model="selectedOrder.orderQuantity" :min="0" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="计划数量">
                            <el-input-number v-model="selectedOrder.plannedQuantity" :min="0" />
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="投料日期">
                            <el-date-picker v-model="selectedOrder.feedingDate" type="date" format="YYYY-MM-DD"
                                value-format="YYYY-MM-DD" placeholder="选择投料日期"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="机加交期">
                            <el-date-picker v-model="selectedOrder.machiningDeliveryDate" type="date"
                                format="YYYY-MM-DD" value-format="YYYY-MM-DD" placeholder="选择机加交期"></el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="自制/外发">
                            <template #label>
                                <span class="required-label">自制/外发</span>
                            </template>
                            <el-select v-model="selectedOrder.manufacturingType" placeholder="请选择自制/外发">
                                <el-option label="自制" value="自制"></el-option>
                                <el-option label="外发" value="外发"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="下单日期">
                            <el-date-picker v-model="selectedOrder.orderDate" type="date" format="YYYY-MM-DD"
                                value-format="YYYY-MM-DD" placeholder="选择下单日期"></el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="上线日期">
                            <el-date-picker v-model="selectedOrder.onlineDate" type="date" format="YYYY-MM-DD"
                                value-format="YYYY-MM-DD" placeholder="选择上线日期"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="订单状态">
                            <template #label>
                                <span class="required-label">订单状态</span>
                            </template>
                            <el-select v-model="selectedOrder.orderStatus" placeholder="请选择订单状态">
                                <el-option label="未完成" value="未完成"></el-option>
                                <el-option label="完成" value="完成"></el-option>
                                <el-option label="异常" value="异常"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">
                    <el-form-item label="备注">
                        <el-input v-model="selectedOrder.remarks" type="textarea" />
                    </el-form-item>
                </el-row>
            </el-form>
            <template v-slot:footer>
                <div style="text-align: right; width: 100%">
                    <el-button v-if="isEditMode" @click="handleDeleteOrder" type="danger">删除</el-button>
                    <el-button @click="handleSubmitOrder" type="primary">{{ isEditMode ? '提交修改' : '添加订单' }}</el-button>
                    <el-button @click="dialogVisible = false">关闭</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 备注编辑弹窗 -->
        <el-dialog v-model="remarkDialogVisible" title="备注详情" width="30%" :show-close="true" @click.stop
            :close-on-click-modal="false" :close-on-press-escape="false">

            <el-input v-model="currentRemark" type="textarea" :rows="4" resize="both"
                :disabled="userRole !== 'admin'" />
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="cancelRemarkEdit">取消</el-button>
                    <el-button type="primary" @click="saveRemark">保存</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMaterialForms, deleteMaterialFormByIds, addMaterialForm, updateMaterialForm, deleteMaterialFormById, exportMaterialForm, validateRole, getMaterialFormCustomers } from '@/api'
import { Search, Refresh, Filter, ArrowDown, Plus, Delete } from '@element-plus/icons-vue'

const materialForms = ref([])  // 物料数据
const selectedRows = ref([])  // 用于保存选中的行
const currentPage = ref(1)  // 当前页
const pageSize = ref(50)  // 每页大小
const totalItems = ref(0)  // 总项数
const dialogVisible = ref(false)  // 弹窗是否可见
const selectedOrder = ref({})  // 当前选中的订单
const isEditMode = ref(false)  // 是否是编辑模式
const remarkDialogVisible = ref(false)
const currentRemark = ref('')
const currentRow = ref(null)
const isSearchBarExpanded = ref(true) // 搜索栏展开状态，默认展开
const materialFormCustomers = ref([]) // 订单客户列表
const filters = ref({
    customer: null,
    parentPartNumber: null,
    copperMaterialNumber: null,
    childPartNumber: null,
    materialSpecification: null,
    materialType: '', // 材质
    orderStatus: '',  // 订单状态
    feedingDate: null,
    machiningDeliveryDate: null,
    orderDate: null,
    onlineDate: null,
    manufacturingType: null
})

// 切换搜索栏展开/折叠状态
const toggleSearchBar = () => {
    isSearchBarExpanded.value = !isSearchBarExpanded.value
}

const downloadExcel = async () => {
    const ids = selectedRows.value.map(row => row.id);
    if (ids.length === 0) {
        ElMessage.warning('请先选择要导出的记录');
        return;
    }

    try {
        const response = await exportMaterialForm(ids);
        const blob = new Blob([response.data], { type: 'application/vnd.ms-excel' });
        const url = window.URL.createObjectURL(blob);

        // 创建下载链接
        const link = document.createElement('a');
        link.href = url;
        link.download = '机加生产计划.xlsx'; // 下载的文件名
        link.click();

        // 释放 URL
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error("导出订单失败:", error);
        alert("导出失败，请稍后重试！");
    }
};

// 搜索订单
const handleSearch = () => {
    currentPage.value = 1
    fetchMaterialForms()
}

const fetchMaterialForms = async () => {
    try {
        const response = await getMaterialForms({
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            customer: filters.value.customer,
            parentPartNumber: filters.value.parentPartNumber,
            copperMaterialNumber: filters.value.copperMaterialNumber,
            childPartNumber: filters.value.childPartNumber,
            materialSpecification: filters.value.materialSpecification,
            orderStatus: filters.value.orderStatus,
            materialType: filters.value.materialType,
            manufacturingType: filters.value.manufacturingType,
            // 日期模糊匹配
            feedingDate: filters.value.feedingDate,
            machiningDeliveryDate: filters.value.machiningDeliveryDate,
            orderDate: filters.value.orderDate,
            onlineDate: filters.value.onlineDate,
        });

        materialForms.value = response.data.data.rows;
        totalItems.value = response.data.data.total;
    } catch (error) {
        console.error('获取物料数据失败:', error);
        ElMessage.error('无法加载物料数据');
    }
}

const handleReset = () => {
    // 重置筛选器
    filters.value.customer = '';
    filters.value.parentPartNumber = '';
    filters.value.copperMaterialNumber = '';
    filters.value.childPartNumber = '';
    filters.value.materialSpecification = '';
    filters.value.manufacturingType = '';
    filters.value.materialType = '';
    filters.value.orderStatus = '';

    filters.value.feedingDate = '';
    filters.value.machiningDeliveryDate = '';
    filters.value.orderDate = '';
    filters.value.onlineDate = '';

    // 重新触发查询
    fetchMaterialForms();
}

const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
    fetchMaterialForms()
}

const handleSelectionChange = (selectedItems) => {
    selectedRows.value = selectedItems;
}

const handleCurrentChange = (val) => {
    currentPage.value = val
    fetchMaterialForms()
}

const getOrderStatusTagType = (status) => {
    if (status === '未完成') {
        return 'danger'
    } else if (status === '完成') {
        return 'success'
    } else {
        return 'warning'
    }
}

const handleRowClick = (row) => {
    selectedOrder.value = { ...row }
    isEditMode.value = true
    dialogVisible.value = true
}

const handleSubmitOrder = async () => {
    // 检查 selectedOrder 是否为空
    if (!selectedOrder.value.customer || !selectedOrder.value.manufacturingType ||!selectedOrder.value.materialType) {
        ElMessage.warning('请完整填写所有必要的字段')
        return
    }
    if (isEditMode.value) {
        // 编辑操作
        await updateMaterialForm(selectedOrder.value.id, selectedOrder.value)
        ElMessage.success('订单修改成功')
    } else {
        // 新增操作
        await addMaterialForm(selectedOrder.value)
        ElMessage.success('订单添加成功')
    }
    fetchMaterialForms()
    dialogVisible.value = false
}

const handleDeleteOrder = async () => {
    try {
        await ElMessageBox.confirm('确定要删除这条订单吗？', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        });

        await deleteMaterialFormById(selectedOrder.value.id);
        ElMessage.success('订单删除成功');
        fetchMaterialForms();
        dialogVisible.value = false;
    } catch (error) {
        if (error === 'cancel') {
            ElMessage.info('已取消删除');
            return;
        }
        console.error('删除失败:', error);
        ElMessage.error('删除失败，请稍后重试');
    }
};

const handleAddOrder = () => {
    selectedOrder.value = {
        customer: '',
        parentPartNumber: '',
        copperMaterialNumber: '',
        childPartNumber: '',
        materialSpecification: '',
        materialType: '',
        quantity: 0,  // 初始化为 0
        copperQuantity: 0,  // 初始化为 0
        orderQuantity: 0,  // 初始化为 0
        plannedQuantity: 0,  // 初始化为 0
        feedingDate: '',
        machiningDeliveryDate: '',
        manufacturingType: '',
        orderDate: new Date().toISOString().split('T')[0],
        onlineDate: '',
        orderStatus: '未完成',
        remarks: ''
    }
    isEditMode.value = false
    dialogVisible.value = true


    selectedRows.value = []  // 清空选中的行
    currentRow.value = null  // 清空当前行 
    currentPage.value = 1
    fetchMaterialForms()  // 刷新列表
    remarkDialogVisible.value = false  // 确保备注弹窗关闭
    currentRemark.value = ''  // 清空备注内容
    materialFormCustomers.value = []  // 清空客户列表
    getMaterialFormCustomersAll()  // 重新获取客户列表
}

const handleBatchDelete = async () => {
    try {
        if (selectedRows.value.length === 0) {
            ElMessage.warning('请先选择要删除的订单');
            return;
        }

        await ElMessageBox.confirm(
            `确定要删除选中的 ${selectedRows.value.length} 条订单吗？`,
            '警告',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        );

        const ids = selectedRows.value.map(row => row.id);
        await deleteMaterialFormByIds(ids);
        ElMessage.success('批量删除成功');
        selectedRows.value = [];  // 清空选中的行
        fetchMaterialForms();  // 刷新列表
    } catch (error) {
        if (error === 'cancel') {
            ElMessage.info('已取消删除');
            return;
        }
        console.error('批量删除失败:', error);
        ElMessage.error('批量删除失败，请稍后重试');
    }
};

const showRemarkDialog = (row) => {
    currentRow.value = row;
    currentRemark.value = row.remarks;
    remarkDialogVisible.value = true;
}

const saveRemark = async () => {
    try {
        await updateMaterialForm(currentRow.value.id, {
            ...currentRow.value,
            remarks: currentRemark.value
        });
        currentRow.value.remarks = currentRemark.value;
        ElMessage.success('备注更新成功');
        remarkDialogVisible.value = false;
    } catch (error) {
        console.error('更新备注失败', error);
        ElMessage.error('更新备注失败，请稍后重试');
    }
};

const cancelRemarkEdit = () => {
    remarkDialogVisible.value = false;
};

//获取订单客户列表
const getMaterialFormCustomersAll = async () => {
    try {
        const res = await getMaterialFormCustomers()
        materialFormCustomers.value = res.data.data
        console.log('获取订单客户列表成功', materialFormCustomers.value);
    } catch (error) {
        console.error('获取订单客户列表失败', error)
    }
}

const getToken = localStorage.getItem('token')
const userRole = ref('')

onMounted(async () => {
    fetchMaterialForms()
    getMaterialFormCustomersAll()
    const res = await validateRole(getToken)
    if (res.data.code === 0) {
        userRole.value = res.data.data // "admin" 或 "user"
    }
})
</script>

<style scoped>
/* Main container styles */
.machine-plan-container {
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

/* Individual input wrapper */
.input-wrapper {
    flex: 1;
    min-width: 200px;
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
.w-full {
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

.remark-text {
    padding: 5px;
    min-height: 24px;
    line-height: 24px;
}

.remark-text:hover {
    background-color: #f5f7fa;
}

:deep(.el-textarea__inner) {
    min-height: 60px;
}

.required-label {
    color: #f56c6c;
    /* Element Plus 默认的红色 */
}
</style>
