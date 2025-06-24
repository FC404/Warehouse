<template>
    <div class="login-container">
        <div class="login-image">
            <!-- 左侧背景图片，文字居中 -->
            <div class="overlay-text">欢迎来到管理系统</div>
        </div>
        <div class="login-form">
            <h2>系统登录</h2>
            <form @submit.prevent="handleLogin">
                <div class="form-group">
                    <label for="username">用户名</label>
                    <input type="text" id="username" v-model="username" placeholder="请输入用户名" required />
                </div>
                <div class="form-group">
                    <label for="password">密码</label>
                    <input type="password" id="password" v-model="password" placeholder="请输入密码" required />
                </div>
                <button type="submit" class="login-button">登录</button>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/api' // 确保你已经导入了 API
import { ElMessage } from 'element-plus'
const username = ref('')
const password = ref('')
const router = useRouter()

const handleLogin = async () => {
    try {
        const response = await api.post('/login', { username: username.value, password: password.value })
        if (response.data.code === 0) {
            localStorage.setItem('token', response.data.data)  // 存储 token
            ElMessage.success('登录成功');
            router.push('/')  // 登录成功后跳转首页
        } else {
            ElMessage.error(response.data.message || '登录失败，请检查用户名或密码');
        }
    } catch (error) {
        alert('登录出错，请稍后再试！')
        console.error(error)
    }
}
</script>

<style scoped>
/* 登录页面容器 */
.login-container {
    display: flex;
    height: 100vh;
}

/* 左侧图片 */
.login-image {
    flex: 1;
    background-image: url('@/assets/login-background2.jpg');
    background-size: cover;
    background-position: center;
    position: relative;
}

/* 半透明遮罩层 */
.login-image::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    /* 半透明遮罩 */
    z-index: 1;
}

/* 白色文字居中 */
.overlay-text {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    color: white;
    font-size: 30px;
    font-weight: bold;
    text-shadow: 2px 2px 5px rgba(0, 0, 0, 0.7);
    /* 加入阴影增加可读性 */
    text-align: center;
    z-index: 2;
}

/* 右侧表单 */
.login-form {
    flex: 0 0 380px;
    background: rgba(255, 255, 255, 0.9);
    padding: 40px 30px;
    border-radius: 10px;
    box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    z-index: 2;
}

/* 标题 */
h2 {
    color: #333;
    font-size: 26px;
    font-weight: 600;
    margin-bottom: 30px;
}

/* 表单项 */
.form-group {
    margin-bottom: 20px;
    text-align: left;
}

/* 标签 */
label {
    display: block;
    font-size: 14px;
    color: #444;
    margin-bottom: 6px;
}

/* 输入框 */
input {
    width: 100%;
    padding: 12px;
    font-size: 16px;
    border: 1px solid #ddd;
    border-radius: 8px;
    outline: none;
    transition: all 0.3s ease;
}

/* 输入框聚焦效果 */
input:focus {
    border-color: #4a90e2;
    box-shadow: 0 0 5px rgba(74, 144, 226, 0.6);
}

/* 登录按钮 */
.login-button {
    width: 100%;
    padding: 15px;
    font-size: 18px;
    font-weight: bold;
    color: white;
    background: #4a90e2;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;
}

/* 按钮悬停效果 */
.login-button:hover {
    background: #357ab7;
}

/* 响应式布局 */
@media (max-width: 768px) {
    .login-container {
        flex-direction: column;
    }

    .login-image {
        height: 40vh;
    }

    .login-form {
        width: 90%;
        padding: 20px;
    }

    h2 {
        font-size: 22px;
    }

    input {
        padding: 10px;
        font-size: 14px;
    }

    .login-button {
        padding: 12px;
        font-size: 16px;
    }
}
</style>