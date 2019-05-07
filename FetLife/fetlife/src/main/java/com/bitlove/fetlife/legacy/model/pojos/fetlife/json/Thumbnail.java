package com.bitlove.fetlife.legacy.model.pojos.fetlife.json;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Picture;
import com.fasterxml.jackson.annotation.JsonProperty;
public class Thumbnail {

    @JsonProperty("variants")
    private PictureVariants variants;


    public Picture getAsPicture(Member member) {
        PictureVariants pictureVariants = new PictureVariants();

        PictureVariant pictureVariant = new PictureVariant();
        pictureVariant.setUrl(variants.get150().getUrl());
        pictureVariants.setMedium(pictureVariant);

        pictureVariant = new PictureVariant();
        pictureVariant.setUrl(variants.get345().getUrl());
        pictureVariants.setLarge(pictureVariant);

        Picture picture = new Picture();
        picture.setMember(member);
        picture.setVariants(pictureVariants);
        return picture;
    }

    @JsonProperty("variants")
    public PictureVariants getVariants() {
        return variants;
    }

    @JsonProperty("variants")
    public void setVariants(PictureVariants variants) {
        this.variants = variants;
    }

}
