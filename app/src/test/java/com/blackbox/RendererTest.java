package com.blackbox;

import com.blackbox.renderer.Renderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RendererTest {
    Renderer renderer;
    @BeforeEach
    void init(){
        Renderer renderer = new Renderer();
    }

    @DisplayName("Create renderer object")
    @Test
    void testCreateRenderer() {
        Renderer r = new Renderer();
    }

    @DisplayName("Check renderer resize")
    @Test
    void checkResize() {
        renderer.resize(600, 100);

        assertEquals(renderer.getWidth(), 600);
        assertEquals(renderer.getHeight(), 100);
    }


}
