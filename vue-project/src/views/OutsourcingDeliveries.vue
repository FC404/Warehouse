<template>
    <div class="outsourcing-deliveries">
        <!-- 可折叠的筛选条件区域 -->
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
                <el-row :gutter="16" class="filter-container" align="middle">
                    <el-col :xs="24" :sm="12" :md="4">
                        <div class="input-wrapper">
                            <label class="input-label">供应商</label>
                            <el-select v-model="filters.supplier" placeholder="请选择供应商" clearable class="w-full">
                                <el-option v-for="item in supplierList" :key="item" :label="item" :value="item" />
                            </el-select>
                        </div>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="4">
                        <div class="input-wrapper">
                            <label class="input-label">产品编码</label>
                            <el-input v-model="filters.productCode" placeholder="产品编码" clearable />
                        </div>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="4">
                        <div class="input-wrapper">
                            <label class="input-label">产品名称</label>
                            <el-input v-model="filters.productName" placeholder="产品名称" clearable />
                        </div>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="4">
                        <div class="input-wrapper">
                            <label class="input-label">外发日期</label>
                            <el-date-picker v-model="filters.outsourceDate" placeholder="选择日期进行查询" type="date"
                                format="YYYY-MM-DD" value-format="YYYY-MM-DD" class="w-full" />
                        </div>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="4">
                        <div class="input-wrapper">
                            <label class="input-label">计划完工日期</label>
                            <el-date-picker v-model="filters.plannedCompletionDate" placeholder="选择日期进行查询" type="date"
                                format="YYYY-MM-DD" value-format="YYYY-MM-DD" class="w-full" />
                        </div>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="4">
                        <div class="input-wrapper">
                            <label class="input-label">回厂日期</label>
                            <el-date-picker v-model="filters.returnDate" placeholder="选择日期进行查询" type="date"
                                format="YYYY-MM-DD" value-format="YYYY-MM-DD" class="w-full" />
                        </div>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="8">
                        <div class="input-wrapper">
                            <label class="input-label">状态</label>
                            <el-select v-model="filters.isOnline" placeholder="选择状态" clearable class="w-full">
                                <el-option label="全部" value="" />
                                <el-option label="上线" value="上线" />
                                <el-option label="未上线" value="未上线" />
                            </el-select>
                        </div>
                    </el-col>
                </el-row>

                <div class="buttons-container">
                    <div class="button-group">
                        <el-button type="primary" @click="getData">
                            <el-icon>
                                <Search />
                            </el-icon>查询
                        </el-button>
                        <el-button @click="resetFilters">
                            <el-icon>
                                <RefreshRight />
                            </el-icon>重置
                        </el-button>
                        <el-button type="primary" @click="downloadExcel">导出 Excel</el-button>
                    </div>

                    <div class="button-group">
                        <el-button type="primary" @click="openAddOrderDialog" v-if="userRole === 'admin'">
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

        <!-- 表格 -->
        <el-table :data="outsourcingDeliveries" style="width: 100%" v-loading="loading" border stripe
            @row-click="handleRowClick" ref="table" v-model:selection="selectedRows"
            @selection-change="handleSelectionChange" :cell-style="{ padding: '5px 0' }">
            <el-table-column type="selection" width="55"></el-table-column>
            <el-table-column label="供应商" width="105" prop="supplier" />
            <el-table-column label="产品编码" width="115" prop="productCode" />
            <el-table-column label="产品名称" width="105" prop="productName" />
            <el-table-column label="外发数量" width="125" prop="outsourceQuantity" />
            <el-table-column label="外发日期" prop="outsourceDate" width="130" />
            <el-table-column label="计划完工日期" prop="plannedCompletionDate" width="140" />
            <el-table-column label="是否上线" prop="isOnline" width="105">
                <template #default="{ row }">
                    <el-tag :type="row.isOnline === '上线' ? 'success' : 'warning'">
                        {{ row.isOnline }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="回厂日期" prop="returnDate" width="130" />
            <el-table-column label="良品数量" prop="goodQuantity" width="105" />
            <el-table-column label="备注">
                <template #default="{ row }">
                    <div class="remark-text" @click.stop="showRemarkDialog(row)" style="cursor: pointer;">
                        {{ row.remarks || '点击查看备注' }}
                    </div>
                </template>
            </el-table-column>
        </el-table>

        <!-- 添加分页组件 -->
        <div style="display: flex; justify-content: center; margin-top: 20px;">
            <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper" :total="totalRows" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" class="pagination" />
        </div>
        <!-- 添加/编辑订单弹窗 -->
        <el-dialog v-model="dialogVisible" :title="currentOutsourcingDelivery.id ? '编辑委外发货' : '新增委外发货'" width="50%"
            :close-on-click-modal="false" :close-on-press-escape="false" v-if="userRole === 'admin'">
            <el-form :model="currentOutsourcingDelivery" label-width="120px">
                <el-form-item label="供应商">
                    <template #label>
                        <span class="required-label">供应商</span>
                    </template>
                    <el-input v-model="currentOutsourcingDelivery.supplier" />
                </el-form-item>
                <el-form-item label="产品编码">
                    <el-input v-model="currentOutsourcingDelivery.productCode" />
                </el-form-item>
                <el-form-item label="产品名称">
                    <el-input v-model="currentOutsourcingDelivery.productName" />
                </el-form-item>
                <el-form-item label="外发数量">
                    <el-input-number v-model="currentOutsourcingDelivery.outsourceQuantity" :min="0" />
                </el-form-item>
                <el-form-item label="外发日期">
                    <el-date-picker v-model="currentOutsourcingDelivery.outsourceDate" type="date" format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item label="计划完工日期">
                    <el-date-picker v-model="currentOutsourcingDelivery.plannedCompletionDate" type="date"
                        format="YYYY-MM-DD" value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item>
                    <template #label>
                        <span class="required-label">*是否上线</span>
                    </template>
                    <el-select v-model="currentOutsourcingDelivery.isOnline">
                        <el-option label="上线" value="上线" />
                        <el-option label="未上线" value="未上线" />
                    </el-select>
                </el-form-item>
                <el-form-item label="回厂日期">
                    <el-date-picker v-model="currentOutsourcingDelivery.returnDate" type="date" format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD" />
                </el-form-item>
                <el-form-item label="良品数量">
                    <el-input-number v-model="currentOutsourcingDelivery.goodQuantity" :min="0" />
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="currentOutsourcingDelivery.remarks" type="textarea" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button v-if="currentOutsourcingDelivery.id" type="danger"
                        @click="deleteOutsourcingDelivery(currentOutsourcingDelivery.id)">
                        删除
                    </el-button>
                    <el-button type="primary" @click="saveOutsourcingDelivery">确定</el-button>
                    <el-button @click="dialogVisible = false">取消</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 备注查看/编辑弹窗 -->
        <el-dialog v-model="remarkDialogVisible" title="备注详情" width="30%" :show-close="true" @click.stop>
            <el-input v-model="currentRemark" type="textarea" :rows="3" resize="both"
                :disabled="userRole !== 'admin'" />
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="cancelRemarkEdit">取消</el-button>
                    <el-button type="primary" @click="saveRemark" :disabled="userRole !== 'admin'">保存</el-button>
                </span>
            </template>
        </el-dialog>

    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getOutsourcingDeliveries, addOutsourcingDelivery, updateOutsourcingDelivery, deleteOutsourcingDeliveryById, deleteOutsourcingDeliveriesByIds, validateRole, exportOutsourcingdeliveries, getAllSuppliers } from '../api';
import { Search, RefreshRight, Plus, Delete, Filter, ArrowDown } from '@element-plus/icons-vue'

const outsourcingDeliveries = ref([]);
const loading = ref(false);
const selectedRows = ref([]);
const currentOutsourcingDelivery = ref({});
const dialogVisible = ref(false);
const remarkDialogVisible = ref(false);
const currentRemark = ref('');
const currentRow = ref(null);
const isSearchBarExpanded = ref(true);

const filters = ref({
    supplier: '',
    productCode: '',
    productName: '',
    outsourceDate: null,
    plannedCompletionDate: null,
    isOnline: '',
    returnDate: null
});

const pageNum = ref(1);
const pageSize = ref(50);
const totalRows = ref(0);

// 分页方法
const handleSizeChange = (val) => {
    pageSize.value = val;
    pageNum.value = 1; // 重置页码为 1
    getData(); // 重新获取数据
};

const handleCurrentChange = (val) => {
    pageNum.value = val;
    getData(); // 重新获取数据
};

// 获取数据
const getData = async () => {
    loading.value = true;
    try {
        const response = await getOutsourcingDeliveries({
            pageNum: pageNum.value,
            pageSize: pageSize.value,
            ...filters.value,
            outsourceDate: filters.value.outsourceDate || null,
            plannedCompletionDate: filters.value.plannedCompletionDate || null,
            returnDate: filters.value.returnDate || null
        });
        outsourcingDeliveries.value = response.data.data.rows;
        totalRows.value = response.data.data.total;
    } catch (error) {
        console.error('获取委外发货数据失败', error);
        ElMessage.error('获取数据失败，请稍后重试');
    } finally {
        loading.value = false;
    }
};

// 重置筛选条件
const resetFilters = () => {
    filters.value = {
        supplier: '',
        productCode: '',
        productName: '',
        outsourceDate: null,
        plannedCompletionDate: null,
        isOnline: '',
        returnDate: null
    };
    getData();
};

// 切换筛选条件区域的展开/收起
const toggleSearchBar = () => {
    isSearchBarExpanded.value = !isSearchBarExpanded.value;
};

// 处理行点击事件
const handleRowClick = (row) => {
    openDialog(row); // 点击行时打开编辑弹窗
};

// 打开添加订单按钮的处理方法
const openAddOrderDialog = () => {
    openDialog(); // 不传参数，表示新增
};

// 打开新增/编辑对话框
const openDialog = (item) => {
    if (item) {
        currentOutsourcingDelivery.value = { ...item }; // 编辑
    } else {
        currentOutsourcingDelivery.value = {
            id: null,
            supplier: '',
            productCode: '',
            productName: '',
            outsourceQuantity: 0,
            outsourceDate: '',
            plannedCompletionDate: '',
            isOnline: '',
            returnDate: '',
            goodQuantity: 0,
            remarks: ''
        }; // 新增
    }
    dialogVisible.value = true; // 显示弹窗
};

// 保存新增/编辑委外发货
const saveOutsourcingDelivery = async () => {
    const form = currentOutsourcingDelivery.value;

    // 必填校验只针对必须字段
    if (!form.supplier || form.supplier.trim() === '') {
        ElMessage.warning('供应商不能为空');
        return;
    }
    if (!form.isOnline || (form.isOnline !== '上线' && form.isOnline !== '未上线')) {
        ElMessage.warning('请选择是否上线（上线 / 未上线）');
        return;
    }

    // 日期字段不强制
    // 如果你想保证格式正确可以额外加判断，这里省略

    try {
        if (form.id) {
            await updateOutsourcingDelivery(form.id, form);
            ElMessage.success('更新成功');
        } else {
            await addOutsourcingDelivery(form);
            ElMessage.success('新增成功');
        }
        dialogVisible.value = false;
        getData();
        loadSuppliers();
    } catch (error) {
        console.error('保存失败', error);
        ElMessage.error('保存失败，请稍后重试');
    }

};



// 删除当前记录
const deleteOutsourcingDelivery = async (id) => {
    try {
        await ElMessageBox.confirm('确定要删除这条记录吗？', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        });
        await deleteOutsourcingDeliveryById(id);
        ElMessage.success('删除成功');
        dialogVisible.value = false;
        getData();
    } catch (error) {
        if (error === 'cancel') {
            ElMessage.info('已取消删除');
            return;
        }
        console.error('删除失败', error);
        ElMessage.error('删除失败，请稍后重试');
    }
};

// 批量删除
const batchDelete = async () => {
    if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要删除的记录');
        return;
    }

    try {
        await ElMessageBox.confirm(
            `确定要删除选中的 ${selectedRows.value.length} 条记录吗？`,
            '警告',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        );
        const idsToDelete = selectedRows.value.map(item => item.id);
        await deleteOutsourcingDeliveriesByIds(idsToDelete);
        ElMessage.success('删除成功');
        getData();
    } catch (error) {
        if (error === 'cancel') {
            ElMessage.info('已取消删除');
            return;
        }
        console.error('批量删除失败', error);
        ElMessage.error('删除失败，请稍后重试');
    }
};

const handleSelectionChange = (val) => {
    selectedRows.value = val;
};

// 显示备注弹窗
const showRemarkDialog = (row) => {
    currentRow.value = row;
    currentRemark.value = row.remarks;
    remarkDialogVisible.value = true;
};

// 保存备注
const saveRemark = async () => {
    try {
        await updateOutsourcingDelivery(currentRow.value.id, {
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

// 取消编辑备注
const cancelRemarkEdit = () => {
    remarkDialogVisible.value = false;
};

const downloadExcel = async () => {

    if (selectedRows.value.length === 0) {
        ElMessage.warning('请先选择要导出的记录');
        return;
    }
    try {
        const response = await exportOutsourcingdeliveries(
            selectedRows.value.map(item => item.id)
        );
        const blob = new Blob(
            [response.data],
            { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }
        );

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = '委外发货数据.xlsx';
        document.body.appendChild(a);
        a.click();

        // 清理
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('导出 Excel 失败', error);
        ElMessage.error('导出失败，请稍后重试');
    }
};
// 供应商下拉框列表
const supplierList = ref([]);

// 获取供应商列表
const loadSuppliers = async () => {
    try {
        const res = await getAllSuppliers();
        supplierList.value = res.data.data || [];
    } catch (error) {
        ElMessage.error('获取供应商失败');
    }
};

// 初始化数据
const getToken = localStorage.getItem('token')
const userRole = ref('')
onMounted(async () => {
    await getData();
    await loadSuppliers();
    const res = await validateRole(getToken)
    if (res.data.code === 0) {
        userRole.value = res.data.data // "admin" 或 "user"
    }

});
</script>

<style scoped>
/* Main container styles */
.outsourcing-deliveries {

    padding: 20px;
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}


/* Search bar */
.search-bar {
    border: 1px solid #e5e7eb;
    border-radius: 4px;
    margin-bottom: 20px;
}

/* Search bar header */
.search-bar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    cursor: pointer;
}

/* Header left */
.header-left {
    display: flex;
    align-items: center;
}

/* Input icon */
.input-icon {
    margin-right: 8px;
    color: #6b7280;
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

/* Filter container */
.filter-container {
    margin-bottom: 16px;
}

/* Input wrapper */
.input-wrapper {
    width: 100%;
    min-width: 180px;
    margin-bottom: 8px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

/* Input label */
.input-label {
    display: block;
    margin-bottom: 4px;
    font-size: 14px;
    color: #333;
}



/* Buttons container */
.buttons-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

/* Button group */
.button-group {
    display: flex;
    gap: 8px;
}

.remark-text {
    padding: 5px;
    min-height: 24px;
    line-height: 24px;
}

.remark-text:hover {
    background-color: #f5f7fa;
}

.search-bar-header h3 {
    font-size: 14px;
    font-weight: 600;
    color: #4b5563;
    margin: 0;
    /* margin-left: 8px; */
}

/* 添加输入框样式 */
:deep(.el-input-group__append) {
    display: flex;
    gap: 5px;
}

:deep(.el-textarea__inner) {
    min-height: 60px;
}

/* 添加分页样式 */
.pagination {
    margin-top: 20px;
    justify-content: flex-end;
}

.required-label {
    color: #f56c6c;
    /* Element Plus 默认的红色 */
}
</style>