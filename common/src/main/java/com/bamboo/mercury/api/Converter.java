package com.bamboo.mercury.api;

public interface Converter<S, T> {

  T convert(S src);

}
