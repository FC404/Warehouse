import axios from 'axios';
const token = localStorage.getItem('token');  // 确保在请求拦截时获取 token

// 创建 API 实例
const api = axios.create({
    baseURL: '/api',
    headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
    },
    timeout: 10000  // 添加超时设置
});
// 修改请求拦截器，添加错误处理
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['token'] = `${token}`;  // 移除多余的空格
        }
        return config;
    },
    error => {
        console.error('请求拦截器错误:', error);
        return Promise.reject(error);
    }
);



// 获取用户列表（支持分页和筛选）
export const getUserList = ({ pageNum, pageSize, username, phone, email, role }) =>
    api.get('/user/list', {
        params: { pageNum, pageSize, username, phone, email, role }
    });
// 更新用户信息
export const updateUser = (user) => api.put('/user/update', user);
// 删除用户（支持单个和批量删除）
export const deleteUser = (ids) => api.delete('/user/delete', { data: { ids } });
// 添加用户
export const addUser = (user) => api.post('/registe/add', user);



// 导出订单
export const exportOrders = (orderIds) => {
    return api.post('/orders/export', orderIds, {
        responseType: 'blob',  // 确保后端返回的是文件流
        headers: {
            'Content-Type': 'application/json',  // 设置请求头为 JSON 格式
        }
    });
};
// 导出订单
export const exportOutsourcingdeliveries = (Ids) => {
    return api.post('/outsourcing-deliveries/export', Ids, {
        responseType: 'blob',  // 确保后端返回的是文件流
        headers: {
            'Content-Type': 'application/json',  // 设置请求头为 JSON 格式
        }
    });
};
// 导出订单
export const exportMaterialForm = (Ids) => {
    return api.post('/materialForm/export', Ids, {
        responseType: 'blob',  // 确保后端返回的是文件流
        headers: {
            'Content-Type': 'application/json',  // 设置请求头为 JSON 格式
        }
    });
};


// 获取订单数据
export const getOrders = ({ pageNum, pageSize, orderNumber, customer, customerPartNumber, factoryPartNumber, productName, status, orderDate, deliverDate }) =>
    api.get('/orders/list', {
        params: {
            pageNum,
            pageSize,
            orderNumber,
            customer,
            customerPartNumber,
            factoryPartNumber,
            productName,
            status,
            orderDate,
            deliverDate
        }
    });

// 创建订单
export const createOrder = (order) => api.post('/orders/add', order);

// 更新订单
export const updateOrder = (id, order) => api.put(`/orders/${id}`, order);

// 删除单个订单
export const deleteOrderById = (id) => api.delete(`/orders/${id}`);

// 批量删除订单
export const batchDeleteOrders = (ids) => {
    return api.delete('/orders/deleteBatch', { data: ids }); // 直接传递数组
};

//获取订单客户
export const getOrderCustomers = () => api.get('/orders/getCustomer');

// 获取所有供应商名称列表
export const getAllSuppliers = () => api.get('/outsourcing-deliveries/suppliers');

//获取机加生产计划客户
export const getMaterialFormCustomers = () => api.get('/materialForm/getCustomer');

// 获取委外发货登记列表
export const getOutsourcingDeliveries = ({ pageNum, pageSize, supplier, productCode, isOnline, productName, outsourceDate, plannedCompletionDate, returnDate }) =>
    api.get('/outsourcing-deliveries/list', { params: { pageNum, pageSize, supplier, productCode, isOnline, productName, outsourceDate, plannedCompletionDate, returnDate } });

// 根据 ID 获取委外发货登记
export const getOutsourcingDeliveryById = (id) =>
    api.get(`/outsourcing-deliveries/${id}`);

// 添加新的委外发货登记
export const addOutsourcingDelivery = (outsourcingDelivery) =>
    api.post('/outsourcing-deliveries', outsourcingDelivery);

// 更新委外发货登记
export const updateOutsourcingDelivery = (id, outsourcingDelivery) =>
    api.put(`/outsourcing-deliveries/${id}`, outsourcingDelivery);

// 删除委外发货登记
export const deleteOutsourcingDeliveryById = (id) =>
    api.delete(`/outsourcing-deliveries/${id}`);

// 批量删除委外发货登记
export const deleteOutsourcingDeliveriesByIds = (ids) =>
    api.delete('/outsourcing-deliveries/batch', { data: ids }, {
        headers: {
            'token': ` ${token}`,  // 设置请求头为 JSON 格式
        }
    });


// 获取机加生产计划数据
export const getMaterialForms = ({ pageNum, pageSize, customer, parentPartNumber, copperMaterialNumber, childPartNumber, materialSpecification, materialType, quantity, copperQuantity, orderQuantity, plannedQuantity, manufacturingType, orderStatus, feedingDate, machiningDeliveryDate, orderDate, onlineDate }) =>
    api.get('/materialForm/list', { params: { pageNum, pageSize, customer, parentPartNumber, copperMaterialNumber, childPartNumber, materialSpecification, materialType, quantity, copperQuantity, orderQuantity, plannedQuantity, manufacturingType, orderStatus, feedingDate, machiningDeliveryDate, orderDate, onlineDate } });

// 根据ID查询机加生产计划
export const getMaterialFormById = (id) =>
    api.get(`/materialForm/${id}`);

// 创建机加生产计划
export const addMaterialForm = (materialForm) =>
    api.post('/materialForm/add', materialForm);

// 更新机加生产计划
export const updateMaterialForm = (id, materialForm) =>
    api.put(`/materialForm/${id}`, materialForm);

// 删除单个机加生产计划
export const deleteMaterialFormById = (id) =>
    api.delete(`/materialForm/${id}`);

// 批量删除机加生产计划
export const deleteMaterialFormByIds = (ids) =>
    api.delete('/materialForm/deleteBatch', { data: ids });

// 获取任务列表
export const getTaskbarData = ({ pageNum, pageSize, taskname, status }) =>
    api.get('/taskbar/list', { params: { pageNum, pageSize, taskname, status } }, {
        headers: {
            'token': ` ${token}`,  // 设置请求头为 JSON 格式
        }
    });
// 添加任务
export const addTask = (taskbar) =>
    api.post('/taskbar/add', taskbar);
// 修改任务
export const updateTask = (taskbar) =>
    api.put('/taskbar/update', taskbar);
// 删除任务
export const deleteTasks = (ids) =>
    api.post('/taskbar/deleteBatch', ids); // 直接传数组
  

// 获取用户总数
export const countUsers = () => api.get('/user/count');
// 获取订单总数
export const countOrders = () => api.get('/orders/count');
// 获取委外发货总数
export const countOutsourcingDelivery = () => api.get('/outsourcing-deliveries/count');
// 获取机加生产计划总数 
export const countMaterialForm = () => api.get('/materialForm/count');
// 获取订单总计数
export const countOrdersByRange = (range) => api.get('/orders/countOrdersByRange', { params: { range } });

//获取操作日志
export const getLogList = ({ pageNum, pageSize }) => api.get('/log/list', { params: { pageNum, pageSize } });



// 验证用户的 token 是否有效
export const validateUser = () => {
    return api.post('/validate-user', {});  // 直接发送请求，不需要显式传递 token
};


// 登录接口
export const login = (credentials) => api.post('/login', credentials);


// 验证用户角色
export const validateRole = (token) => {
    console.log('发送验证请求，token:', token); // 添加调试日志
    return api.post('/validate-role', {}, {
        headers: {
            'token': `${token}`
        }
    }).catch(error => {
        console.error('验证角色请求失败:', error);
        throw error;
    });
};

// 获取当前登录用户的用户名
export const getUsername = async () => {
    const token = localStorage.getItem('token');  // 从 localStorage 获取 token
    if (!token) {
        return null; // 如果没有 token，则返回 null
    }
    try {
        // 调用后端接口获取用户名
        const response = await api.get('/get-user-info', {
            headers: {
                'token': `${token}`
            }
        });
        if (response.data.code === 0) {
            return response.data.data;  // 返回获取到的用户名
        } else {
            console.error("获取用户名失败", response.data.message);
            return null;
        }
    } catch (e) {
        console.error("获取用户名失败", e);
        return null;  // 如果请求失败，则返回 null
    }
};


// 退出登录接口
export const logout = async (token) => {
    try {
        // 请求登出接口
        await api.delete('/logout', {
            headers: {
                'token': `${token}`
            }
        });

        // 清除本地存储中的 token
        localStorage.removeItem('token');
        return { success: true };
    } catch (e) {
        console.error("退出登录失败", e);
        return { success: false, message: '退出登录失败' };
    }
};




export default api;
