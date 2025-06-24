<template>
    <div class="not-found">
        <div class="content">
            <h1>Oops!</h1>
            <h3>The page you're looking for doesn't exist.</h3>
            <router-link to="/" class="btn">Back to Home</router-link>
        </div>
        <div class="illustration">
            <!-- 这里可以插入一张404的插图或使用CSS绘制图形 -->
            <svg width="380px" height="500px" viewBox="0 0 837 1045" version="1.1" xmlns="http://www.w3.org/2000/svg"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                style="fill-rule:evenodd;clip-rule:evenodd;stroke-linecap:round;stroke-linejoin:round;stroke-miterlimit:1.5;">
                <!-- SVG Path Data Here, or remove this if not using SVG -->
            </svg>
        </div>
    </div>
</template>

<script>
export default {
    name: 'NotFound',
};
</script>

<style scoped>
.not-found {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 40px;
    font-family: 'Arial', sans-serif;
}

.content {
    max-width: 50%;
}

h1 {
    font-size: 86px;
    color: #ff4f4f;
    margin-bottom: 20px;
}

h3 {
    font-size: 24px;
    color: #555;
    margin-bottom: 40px;
}

.btn {
    display: inline-block;
    padding: 10px 30px;
    font-size: 16px;
    cursor: pointer;
    text-decoration: none;
    background-color: #ff4f4f;
    color: white;
    border-radius: 5px;
    transition: background-color 0.3s ease;
}

.btn:hover {
    background-color: #e04242;
}

.illustration {
    width: 50%;
    text-align: right;
}

/* 如果你想要添加一些动画效果 */
@keyframes slideInFromLeft {
    0% {
        transform: translateX(-100%);
        opacity: 0;
    }

    100% {
        transform: translateX(0);
        opacity: 1;
    }
}

.content,
.illustration {
    animation: slideInFromLeft 1s ease-out;
}
</style>