/** 查询处理器 */
class SearchHandler {
    /** 控制中心 */
    #cc
    /** 请求查询的输入和确认按钮 */
    #requestInput; #requestSearch

    constructor(controlCenter) {
        this.#cc = controlCenter
    }

    initRequestComponent(requestInput, requestSearch) {
        this.#requestInput = requestInput
        this.#requestSearch = requestSearch
        this.#requestSearchOnclick()
    }

    #requestSearchOnclick() {
        this.#requestSearch.bind("click", () => {
            // 拿到要搜索的内容
            let key = this.#cc.getData(REQUEST_INPUT, "requestInput")
            searchUser(key, (data) => {
                // 将内容返回请求搜索区
                this.#cc.setData(REQUEST_SEARCH_SERVICE, "result", data.data.result)
            })
        })
    }

}