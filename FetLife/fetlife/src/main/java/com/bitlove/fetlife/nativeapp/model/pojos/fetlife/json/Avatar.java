package com.bitlove.fetlife.nativeapp.model.pojos.fetlife.json;

import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Picture;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Avatar {

    @JsonProperty("variants")
    private AvatarVariants variants;

    public AvatarVariants getVariants() {
        return variants;
    }

    public void setVariants(AvatarVariants variants) {
        this.variants = variants;
    }

    public Picture getAsMediumPicture(Member member) {
        PictureVariant pictureVariant = new PictureVariant();
        pictureVariant.setUrl(variants.getMediumUrl());
        PictureVariants pictureVariants = new PictureVariants();
        pictureVariants.setMedium(pictureVariant);
        Picture picture = new Picture();
        picture.setMember(member);
        picture.setVariants(pictureVariants);
        return picture;
    }
}
