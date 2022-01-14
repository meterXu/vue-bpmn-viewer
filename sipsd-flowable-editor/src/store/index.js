const bpmn = {
    state: {
        form:{},
        timeData:null,
        options:'111'
    },
    mutations:{
        setForm(state,mapMode) {
            state.form = mapMode
        },
        setTimeData(state,mapMode) {
            state.timeData = mapMode
        },
        setOptions(state,mapMode) {
            state.options = mapMode
        },
    },
    actions:{

    },
    getters:{

    }
}

export default bpmn
