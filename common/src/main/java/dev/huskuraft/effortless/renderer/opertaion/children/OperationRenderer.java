package dev.huskuraft.effortless.renderer.opertaion.children;

import dev.huskuraft.effortless.api.renderer.Renderer;

public interface OperationRenderer {

    void render(Renderer renderer, RendererParams rendererParams, float deltaTick);

}
