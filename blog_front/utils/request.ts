const service = $fetch.create({
    baseURL: 'http://localhost:8080',

    async onRequest(request) {
        // 请求显示
        request.options.headers = {
            'Content-Type': 'application/json;charset=UTF-8',
            Authorization: localStorage.getItem('token') || ''
        }
    },
    async onResponse(response) {
        return response.response._data
    }
})
export default service;