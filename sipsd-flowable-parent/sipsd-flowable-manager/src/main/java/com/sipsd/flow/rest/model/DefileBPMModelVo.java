//package com.sipsd.flow.rest.model;
//
//import java.io.Serializable;
//
//import org.apache.commons.codec.binary.Base64;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Getter
//@Setter
//@NoArgsConstructor
//public class DefileBPMModelVo implements Serializable {
//    private String modelId;
//    private String modelName;
//    private String thumbnail;
//    private String modelKey;
//    private int modelType;
//    private String description;
//    private int version;
//    private String modelEditorJson;
//
//
//    public DefileBPMModelVo with(org.flowable.ui.modeler.domain.Model model) {
//        if(model==null){
//            return this;
//        }
//        this .setModelId(model.getId());
//        this.setModelName(model.getName());
//        this.setModelKey(model.getKey());
//        this.setThumbnail(Base64.encodeBase64String(model.getThumbnail()));
//        this.setModelType(model.getModelType());
//        this.setDescription(model.getDescription());
//        this.setVersion(model.getVersion());
//        this.setModelEditorJson(model.getModelEditorJson());
//        return  this;
//    }
//}
