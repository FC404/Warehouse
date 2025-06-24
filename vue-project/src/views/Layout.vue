<template>
    <el-container class="app-wrapper">
        <el-aside class="sidebar" :class="{ collapsed: isCollapse }">
            <div class="sidebar-header">
                <img src="../assets/warehouse-icon.png" alt="仓库管理" class="header-logo-img" />
                <span class="sidebar-title" :class="{ hidden: isCollapse }">仓库管理</span>
            </div>

            <el-menu :default-active="activeIndex" class="el-menu-vertical" :collapse="isCollapse"
                @select="handleSelect">
                <el-menu-item index="dashboard">
                    <el-icon>
                        <DataLine />
                    </el-icon>
                    <template #title>仪表盘</template>
                </el-menu-item>
                <el-menu-item index="user">
                    <el-icon>
                        <Box />
                    </el-icon>
                    <template #title>用户管理</template>
                </el-menu-item>
                <el-menu-item index="orders">
                    <el-icon>
                        <ShoppingCart />
                    </el-icon>
                    <template #title>订单管理</template>
                </el-menu-item>
                <el-menu-item index="outsourcingDeliveries">
                    <el-icon>
                        <Van />
                    </el-icon>
                    <template #title>委外发货</template>
                </el-menu-item>
                <el-menu-item index="materialForm">
                    <el-icon>
                        <Document />
                    </el-icon>
                    <template #title>机加生产计划</template>
                </el-menu-item>
                <el-menu-item index="taskbar">
                    <el-icon>
                        <TakeawayBox />
                    </el-icon>
                    <template #title>任务栏</template>
                </el-menu-item>
                <el-menu-item index="log">
                    <el-icon>
                        <Setting />
                    </el-icon>
                    <template #title>操作日志</template>
                </el-menu-item>
            </el-menu>

            <el-button v-show="showBackTop" color="#303133" class="back-to-top-sidebar" :icon="ArrowUpBold" circle
                @click="scrollMainToTop" />
        </el-aside>

        <el-container>
            <el-header class="header">
                <div @click="toggleSidebar" class="toggle-btn">
                    <el-icon>
                        <Fold v-if="isCollapse" />
                        <Expand v-else />
                    </el-icon>
                </div>
                <h1 class="title animate-title">{{ currentTitle }}</h1>

                <el-dropdown @command="handleCommand" class="user-dropdown">
                    <span class="el-dropdown-link">
                        {{ username || '加载中...' }}
                        <el-icon class="el-icon--right">
                            <ArrowDown />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-item command="logout">登出</el-dropdown-item>
                    </template>
                </el-dropdown>
            </el-header>

            <el-main>
                <!-- 滚动容器 -->
                <div class="scroll-content" ref="scrollContainer">
                    <router-view />
                </div>
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
    DataLine, Box, ShoppingCart, TakeawayBox, Document, Menu,
    ArrowDown, Fold, Expand, Van, Setting, ArrowUpBold
} from '@element-plus/icons-vue'
import { logout, getUsername } from '@/api'
import { watch } from 'vue'
const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)
const username = ref('')
const currentTitle = ref('Warehouse')

const scrollContainer = ref(null)
const showBackTop = ref(false)

const titleMap = {
    dashboard: '仪表盘',
    user: '用户管理',
    orders: '订单管理',
    outsourcingDeliveries: '委外发货',
    materialForm: '机加生产计划',
    taskbar: '任务栏',
    log: '操作日志',
}


const toggleSidebar = () => {
    isCollapse.value = !isCollapse.value
}
const fetchUsername = async () => {
    try {
        const user = await getUsername()
        username.value = user || '用户未登录'
    } catch (error) {
        console.error('获取用户名失败:', error)
        username.value = '获取用户名失败'
    }
}

const handleScroll = () => {
    const el = scrollContainer.value
    if (el) {
        showBackTop.value = el.scrollTop > 100
    }
}

const scrollMainToTop = () => {
    scrollContainer.value?.scrollTo({ top: 0, behavior: 'smooth' })
}
watch(() => route.name, (newName) => {
    currentTitle.value = titleMap[newName] || 'Warehouse'
}, { immediate: true })
onMounted(() => {
    fetchUsername()
    scrollContainer.value?.addEventListener('scroll', handleScroll)
})

onBeforeUnmount(() => {
    scrollContainer.value?.removeEventListener('scroll', handleScroll)
})

const activeIndex = computed(() => route.name)
const handleSelect = (key) => {
    router.push({ name: key })
    currentTitle.value = titleMap[key] || 'Warehouse'
}
const handleCommand = async (command) => {
    if (command === 'logout') {
        try {
            const token = localStorage.getItem('token')
            if (!token) return alert('Token 不存在，无法退出登录')
            await logout(token)
            localStorage.removeItem('token')
            router.push('/login')
        } catch (error) {
            console.error('退出登录失败', error)
            alert('退出登录失败，请稍后再试')
        }
    }
}
</script>

<style scoped>
.app-wrapper {
  height: 100vh;
  display: flex;
  flex-direction: row;
  overflow: hidden;
}

.el-main {
  background-color: #fff;
  padding: 0;
  overflow: hidden;
}

.scroll-content {
  height: calc(100vh - 54px);
  overflow-y: auto;
  padding: 20px;
}

.sidebar {
  background-color: #fff;
  max-width: 200px;
  transition: max-width 0.5s ease;
  flex-shrink: 0;
  overflow: hidden;
  border-right: 1px solid #aeaeae;
  position: relative;

}

.sidebar.collapsed {
    width: 64px;
}
.sidebar-title,
.el-menu-item > .menu-text {
  transition: opacity 0.3s ease;
  white-space: nowrap;
  color: #444;
  font-weight: 500;
}

.sidebar.collapsed .sidebar-title,
.sidebar.collapsed .el-menu-item > .menu-text {
  opacity: 0;
  pointer-events: none;
  user-select: none;
}

/* 标题和菜单文字的动画 */
.sidebar-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px dashed #ddd;
}

.el-menu-item {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  transition: justify-content 0.2s ease;
}
.sidebar.collapsed .el-menu-item {
    transition: justify-content 0.2s ease;
  justify-content: center;
}
.header-logo-img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
  flex-shrink: 0;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  white-space: nowrap;
  color: #444;
  transition: opacity 0.5s ease, transform 0.5s ease;
  position: relative;
}

/* 菜单文字样式 */
.el-menu-item > .menu-text {
  margin-left: 8px;
  white-space: nowrap;
  transition: opacity 0.5s ease, transform 0.5s ease;
  position: relative;
  color: #444;
  font-weight: 500;
}

/* 折叠时隐藏文字 */
.sidebar.collapsed .sidebar-title,
.sidebar.collapsed .el-menu-item > .menu-text {
  opacity: 0;
  transform: translateX(-20px);
  pointer-events: none;
  user-select: none;
}

/* 菜单项样式 */
.sidebar .el-menu {
  background-color: transparent;
  color: #444;
  border-right: none;
}

.sidebar .el-menu-item {
  background-color: transparent;
  color: #444;
  border-radius: 4px;
  transition: background-color 0.3s;
  display: flex;
  align-items: center;
}

.sidebar .el-menu-item:hover {
  background-color: #f0f0f0;
}

.sidebar .el-menu-item.is-active {
  background-color: #cee21e;
  color: #333;
  font-weight: 600;
}

.header {
  height: 54px;
  background-color: #fff;
  color: #444;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.title {
  font-weight: 700;
  font-size: 22px;
  color: #444;
}

.toggle-btn {
  background: transparent;
  border: none;
  color: #666;
  cursor: pointer;
  font-size: 18px;
}

.el-dropdown-link {
  color: #444;
  cursor: pointer;
}

.back-to-top-sidebar {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}
</style>