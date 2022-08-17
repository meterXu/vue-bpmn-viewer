import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

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

const store = new Vuex.Store({
    modules:{
        bpmn:bpmn
    }
})

export default store
