// Créer ce fichier test
package com.pcagrad.magic.main;

import lombok.Data;

@Data
public class TestLombok {
    private String name;

    public static void main(String[] args) {
        TestLombok test = new TestLombok();
        test.setName("Test"); // Si compile = Lombok OK
        System.out.println(test.getName());
    }
}