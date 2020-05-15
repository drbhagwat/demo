package com.example.demo.config;

import com.example.demo.models.AddressKey;
import org.springframework.core.convert.converter.Converter;

public class StringToAddressKeyConverter implements Converter<String,
    AddressKey> {

  @Override
  public AddressKey convert(String from) {
    String temp = from.replace("AddressKey(", "");
    temp = temp.substring(0, temp.length() - 1);
    String[] data = temp.split(",");
    String[] params = new String[data.length];

    for (int i = 0; i < data.length; i++) {
      params[i] = data[i].split("=")[1];
    }
    return new AddressKey(params[0], params[1], params[2], params[3]);
  }
}
