<template>
    <div class="user-management-container">


        <!-- 搜索筛选区域 -->
        <div class="search-bar">
            <div class="search-bar-header" @click="toggleSearchPanel">
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
                        <ArrowDown :class="{ 'rotate-180': isSearchExpanded }" />
                    </el-icon>
                </div>
            </div>

            <div v-show="isSearchExpanded" class="search-content">
                <div class="search-inputs">
                    <div class="input-wrapper">
                        <label class="input-label">用户名</label>
                        <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable class="search-input" />
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">邮箱</label>
                        <el-input v-model="searchForm.email" placeholder="请输入邮箱" clearable class="search-input" />
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">手机号</label>
                        <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable class="search-input" />
                    </div>

                    <div class="input-wrapper">
                        <label class="input-label">角色</label>
                        <el-select v-model="searchForm.role" placeholder="请选择角色" clearable class="search-input">
                            <el-option label="管理员" value="admin" />
                            <el-option label="用户" value="user" />
                        </el-select>
                    </div>
                </div>

                <div class="buttons-container">
                    <div class="button-group">
                        <el-button type="primary" @click="handleSearch">
                            <el-icon>
                                <Search />
                            </el-icon>搜索
                        </el-button>
                        <el-button @click="resetSearch">
                            <el-icon>
                                <Refresh />
                            </el-icon>重置
                        </el-button>
                    </div>

                    <div class="button-group">
                        <el-button type="primary" @click="handleAddUser" v-if="userRole === 'admin'">
                            <el-icon>
                                <Plus />
                            </el-icon>添加用户
                        </el-button>
                        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedUsers.length === 0"
                            v-if="userRole === 'admin'">
                            <el-icon>
                                <Delete />
                            </el-icon>批量删除
                        </el-button>

                    </div>
                </div>
            </div>
        </div>

        <!-- 表格区域 -->
        <div class="table-section">
            <el-table :data="UserData" @selection-change="handleSelectionChange" border stripe style="width: 100%">
                <el-table-column type="selection" width="50" />
                <el-table-column prop="username" label="用户名" />
                <el-table-column prop="email" label="邮箱" />
                <el-table-column prop="phone" label="手机号" />
                <el-table-column prop="role" label="角色">
                    <template #default="{ row }">
                        <el-tag :type="row.role === 'admin' ? 'danger' : 'success'">
                            {{ row.role === 'admin' ? '管理员' : '用户' }}
                        </el-tag>
                    </template>
                </el-table-column>

                <el-table-column label="操作" fixed="right" width="200">
                    <template #default="scope">
                        <el-button type="primary" link @click="handleEdit(scope.row)" v-if="userRole === 'admin'">
                            编辑
                        </el-button>
                        <el-button type="primary" link @click="handleView(scope.row)">
                            查看
                        </el-button>
                        <el-button type="danger" link @click="handleDelete(scope.row)" v-if="userRole === 'admin'">
                            <el-icon>
                                <Delete />
                            </el-icon>删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div style="display: flex; justify-content: center; margin-top: 20px;">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                :page-sizes="[5, 10, 20, 100]" layout="total, sizes, prev, pager, next, jumper" :total="total"
                @size-change="handleSizeChange" @current-change="handleCurrentChange" class="pagination" />
            </div>  
        </div>

        <!-- 用户表单对话框 -->
        <el-dialog v-model="dialogVisible"
            :title="dialogType === 'add' ? '添加用户' : dialogType === 'edit' ? '编辑用户' : '用户详情'" width="600px"
            :close-on-click-modal="false">
            <el-form ref="userFormRef" :model="userForm" :rules="userFormRules" label-width="100px"
                :disabled="dialogType === 'view'">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="userForm.username" placeholder="请输入用户名" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="userForm.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="userForm.phone" placeholder="请输入手机号码" />
                </el-form-item>
                <el-form-item label="角色" prop="role">
                    <el-select v-model="userForm.role" placeholder="请选择角色">
                        <el-option label="管理员" value="admin" />
                        <el-option label="用户" value="user" />
                    </el-select>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="userForm.password" placeholder="请输入密码" type="password" />
                </el-form-item>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button v-if="dialogType !== 'view'" type="primary" @click="submitForm">确认</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, updateUser, deleteUser, addUser, validateRole } from '../api.js'
import { Search, Refresh, Filter, ArrowDown, Plus, Delete } from '@element-plus/icons-vue'
const currentPage = ref(1) // 当前页
const pageSize = ref(5) // 每页显示数量
const UserData = ref([])
const total = ref(0)
const selectedUsers = ref([])

// 搜索面板展开状态
const isSearchExpanded = ref(true)
const toggleSearchPanel = () => {
    isSearchExpanded.value = !isSearchExpanded.value
}

// 搜索表单
const searchForm = reactive({
    username: '',
    phone: '',
    role: '',
    email: ''
})

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add', 'edit', 'view'
const userFormRef = ref(null)
const userForm = reactive({
    id: null,
    username: '',
    email: '',
    phone: '',
    role: '',
    password: ""
})

// 表单验证规则
const userFormRules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    phone: [
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ],
    role: [
        { required: true, message: '请选择角色', trigger: 'change' }
    ], password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
    ]

}
// 获取本地存储的 token
const getToken = localStorage.getItem('token')
const userRole = ref('')
// 初始化加载数据
onMounted(async () => {
    const res = await validateRole(getToken)
    if (res.data.code === 0) {
        userRole.value = res.data.data // "admin" 或 "user"
    }
    fetchUserList()
})

// 获取用户列表
const fetchUserList = async () => {
    await validateRole(getToken)

    try {
        const response = await getUserList({
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            username: searchForm.username || undefined,
            email: searchForm.email || undefined,
            phone: searchForm.phone || undefined,
            role: searchForm.role || undefined,
        })
        UserData.value = response.data.data.rows
        total.value = response.data.data.total

    } catch (error) {
        console.error('获取用户数据失败:', error)
        ElMessage.error('无法加载用户数据')
    }
}

// 搜索
const handleSearch = () => {
    currentPage.value = 1
    fetchUserList()
}

// 重置搜索
const resetSearch = () => {
    searchForm.username = ''
    searchForm.phone = ''
    searchForm.role = ''
    searchForm.email = ''
    handleSearch()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
    selectedUsers.value = selection
}

// 分页大小变化
const handleSizeChange = (size) => {
    pageSize.value = size
    fetchUserList()
}

// 当前页变化
const handleCurrentChange = (page) => {
    currentPage.value = page
    fetchUserList()
}

// 修改添加用户的方法
const handleAddUser = () => {
    dialogType.value = 'add'
    resetUserForm()
    dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row) => {
    dialogType.value = 'edit'
    resetUserForm()
    // 确保 id 是数字类型
    userForm.id = Number(row.id)
    Object.assign(userForm, row)
    dialogVisible.value = true
}

// 查看用户
const handleView = (row) => {
    dialogType.value = 'view'
    resetUserForm()
    Object.assign(userForm, row)
    dialogVisible.value = true
}

// 删除用户
const handleDelete = (row) => {
    ElMessageBox.confirm(
        `确定要删除用户 ${row.username} 吗？`,
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            // 确保将 row.id 包装在数组中
            await deleteUser([row.id])  // 这里确保传递的是数组
            ElMessage.success('删除成功')
            fetchUserList()
        } catch (error) {
            console.error('删除用户失败', error)
            ElMessage.error('删除用户失败')
        }
    }).catch(() => {
        // 取消删除
    })
}

// 批量删除
const handleBatchDelete = () => {
    if (selectedUsers.value.length === 0) {
        ElMessage.warning('请选择要删除的用户')
        return
    }
    const usernames = selectedUsers.value.map(user => user.username).join('、')
    ElMessageBox.confirm(
        `确定要删除以下用户吗？\n${usernames}`,
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            const ids = selectedUsers.value.map(user => user.id)
            await deleteUser(ids)
            ElMessage.success('批量删除成功')
            fetchUserList()
        } catch (error) {
            console.error('批量删除用户失败', error)
            ElMessage.error('批量删除用户失败')
        }
    }).catch(() => {
        // 取消删除
    })
}

// 重置用户表单
const resetUserForm = () => {
    userForm.id = null // ✅ 避免 id 变成 string
    userForm.username = ''
    userForm.email = ''
    userForm.phone = ''
    userForm.role = ''
    userForm.password = ''

    if (userFormRef.value) {
        userFormRef.value.resetFields()
    }
}

// 提交表单的方法保持不变
const submitForm = async () => {
    if (!userFormRef.value) return
    await userFormRef.value.validate(async (valid) => {
        if (valid) {
            try {
                const userData = {
                    id: userForm.id,  // 确保有 ID
                    username: userForm.username,
                    email: userForm.email,
                    phone: userForm.phone,
                    role: userForm.role,
                    password: userForm.password
                }
                if (dialogType.value === 'add') {
                    const res = await addUser(userData)
                    if (res.data.msg == "用户名已存在") {
                        ElMessage.error('用户名已存在')
                    } else {
                        ElMessage.success('添加用户成功')
                    }

                } else if (dialogType.value === 'edit') {
                    await updateUser(userData)
                    ElMessage.success('更新用户成功')
                }
                dialogVisible.value = false
                fetchUserList()
            } catch (error) {
                console.error('操作失败', error)
                ElMessage.error('操作失败')
            }
        } else {
            ElMessage.warning('请填写完整的信息')
            return false
        }
    })
}
</script>

<style scoped>
/* Main container styles */
.user-management-container {
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

/* Table section */
.table-section {
    margin-bottom: 20px;
}

/* Pagination */
.pagination {
    margin-top: 20px;
    text-align: right;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
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