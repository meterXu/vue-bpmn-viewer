import ZoomScrollModule from 'diagram-js/lib/navigation/zoomscroll/ZoomScroll.js'

ZoomScrollModule.prototype.scroll = function scroll(obj){
    if(obj.dy>0){
        if(this._canvas){
            let zoom = this._canvas.zoom()
            this._canvas.zoom(zoom + 0.2);
        }

    }else {
        if(this._canvas){
            let zoom = this._canvas.zoom()
            this._canvas.zoom(zoom - 0.2);
        }
    }

}

export default {
    __init__: ['zoomScroll'],
    zoomScroll: ['type', ZoomScrollModule]
}
