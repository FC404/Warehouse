<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" :xs="24" v-for="(item, index) in stats" :key="index">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>{{ item.label }}</span>
            </div>
          </template>
          <div class="card-content">
            <h3>{{ item.value }}</h3>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单统计卡片 -->
    <el-card class="box-card chart-card">
      <template #header>
        <div class="card-header chart-header">
          <span>订单统计</span>
          <!-- 修改这部分替换 el-select -->
          <el-button-group>
            <el-button :type="range === 'week' ? 'primary' : 'default'" @click="changeRange('week')">一周</el-button>
            <el-button :type="range === 'month' ? 'primary' : 'default'" @click="changeRange('month')">一个月</el-button>
            <el-button :type="range === 'year' ? 'primary' : 'default'" @click="changeRange('year')">一年</el-button>
          </el-button-group>

        </div>
      </template>
      <div class="card-content chart-content">
        <div ref="orderChart" class="chart-container"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { countUsers, countOrders, countOutsourcingDelivery, countMaterialForm, countOrdersByRange } from '../api.js'
import * as echarts from 'echarts'

const userCount = ref(0)
const ordersCount = ref(0)
const outsourcingDelivery = ref(0)
const materialForm = ref(0)
const orderChart = ref(null)
const range = ref('month')

const stats = computed(() => [
  { label: '用户数量', value: userCount.value },
  { label: '订单数量', value: ordersCount.value },
  { label: '委外发货', value: outsourcingDelivery.value },
  { label: '机加生产计划', value: materialForm.value },
])
function changeRange(value) {
  if (range.value !== value) {
    range.value = value
    fetchOrdersByRange()
  }
}

const fetchOrdersByRange = async () => {
  try {
    const res = await countOrdersByRange(range.value)
    if (res.data.data && Array.isArray(res.data.data)) {
      initChart(res.data.data)
    }
  } catch (error) {
    console.error('加载订单统计失败', error)
  }
}

onMounted(async () => {
  try {
    fetchOrdersByRange()

    const user = await countUsers()
    userCount.value = user.data.data

    const order = await countOrders()
    ordersCount.value = order.data.data

    const outsourcing = await countOutsourcingDelivery()
    outsourcingDelivery.value = outsourcing.data.data

    const material = await countMaterialForm()
    materialForm.value = material.data.data
  } catch (error) {
    console.error('数据加载失败:', error)
  }
})

const initChart = (data) => {
  const chart = echarts.init(orderChart.value)
  //转中文
  range.value = range.value === 'week' ? '一周' : range.value === 'month' ? '一个月' : '一年'
  const option = {
    title: {
      text: `最近${range.value}订单统计`,
      left: 'center',
      textStyle: {
        fontWeight: 'bold',
        fontSize: 18,
      },
    },
    tooltip: {
      trigger: 'axis',
    },
    grid: {
      left: '10%',
      right: '10%',
      bottom: '15%',
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date),
      axisLabel: {
        rotate: 0, // 改为水平显示
        color: '#555',
      },
      axisLine: {
        lineStyle: {
          color: '#ccc',
        },
      },
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLabel: {
        formatter: value => Math.floor(value),
        color: '#555',
      },
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#eee',
        },
      },
    },
    series: [{
      data: data.map(item => item.count),
      type: 'line',
      smooth: true,
     
      symbolSize: 8,
    }],
  }

  chart.setOption(option)
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background-color: #f9f9fb;
  min-height: 100vh;
}

.stats-row {
  margin-bottom: 30px;
}

.box-card {
  border-radius: 10px;
  box-shadow: 0 4px 8px rgb(0 0 0 / 0.1);
  transition: box-shadow 0.3s ease;
}

.box-card:hover {
  box-shadow: 0 6px 14px rgb(0 0 0 / 0.15);
}

.card-header {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.card-content {
  text-align: center;
  font-size: 32px;
  color: #222;
  padding: 7px 0;
  font-weight: 700;
  user-select: none;
}

.chart-card {
  padding: 20px;
  background-color: #fff;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.range-select {
  width: 160px;
  border-radius: 6px;
  font-size: 14px;
}

.range-select .el-input__inner {
  border-color: #1890ff;
  transition: border-color 0.3s;
}

.range-select .el-input__inner:hover {
  border-color: #40a9ff;
}

.chart-content {
  padding-top: 20px;
}

.chart-container {
  width: 100%;
  height: 320px;
}
</style>