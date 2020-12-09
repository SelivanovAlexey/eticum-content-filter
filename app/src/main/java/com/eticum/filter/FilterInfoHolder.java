package com.eticum.filter;

import com.eticum.api.http.model.Profile;
import com.eticum.api.http.model.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FilterInfoHolder {

    private User user;
    private Profile profile;

    private static class Holder {
        static volatile FilterInfoHolder instance = new FilterInfoHolder();
    }

    public static FilterInfoHolder get(){
        return Holder.instance;
    }


}
