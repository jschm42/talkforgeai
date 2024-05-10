/*
 * Copyright (c) 2024 Jean Schmitz.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talkforgeai.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.jupiter.api.Test;

public class ByteConvertTest {


  @Test
  public void testByteConvert() {
    double[] embeddingArray = new double[]{-0.0147974, 0.044764798, -4.0669565E-4,
        0.021996494,};

    byte[] embedingBytes;
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos)) {
      out.writeObject(embeddingArray);
      embedingBytes = bos.toByteArray();
      System.out.println("Embedding bytes: " + embedingBytes);
    } catch (IOException e) {
      throw new RuntimeException("Error writing embedding to byte array", e);
    }

    try (ByteArrayInputStream bis = new ByteArrayInputStream(embedingBytes);
        ObjectInputStream in = new ObjectInputStream(bis)) {
      var converted = (double[]) in.readObject();
      for (double d : converted) {
        System.out.println("Conv: " + d);
      }
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("Error reading embedding from byte array", e);
    }

  }

}
