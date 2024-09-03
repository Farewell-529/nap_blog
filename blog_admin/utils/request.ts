const service = $fetch.create({
    baseURL: 'http://localhost:8080',

    async onRequest(request) {
        // 请求显示
        request.options.headers = {
            'Content-Type': 'application/json;charset=UTF-8',
            Authorization: localStorage.getItem('token') || ''
        }
        //如果是上传文件请求
        if (request.options.body instanceof FormData) {
            delete request.options.headers['Content-Type'];
        }
    },
    async onResponse(response) {
        const res = response.response._data
        if (res.code === 403) {
            useRouter().push("/login/")
            console.error(res)
            return res.data
        }
        return res
    }
})
export default service;