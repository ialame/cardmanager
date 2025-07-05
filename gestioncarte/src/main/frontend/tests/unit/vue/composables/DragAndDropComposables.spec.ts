import {DragAndDropComposables} from "@/vue/composables/DragAndDropComposables";
import {fireEvent} from "@testing-library/dom";
import {TestHelper} from "@tu/helpers";
import {describe, expect, it, vi} from 'vitest';

const dragDiv = document.createElement('div');
const dropDiv = document.createElement('div');

dropDiv.getBoundingClientRect = vi.fn(() => TestHelper.createRect(0, 0, 100, 100));

const {x, y, isDragging} = DragAndDropComposables.useDrag('test', dragDiv, {test: 0});

describe('DragAndDropComposables.useDrag', () => {
    fireEvent.pointerDown(dragDiv, { clientX: 0, clientY: 0 });
    it.each([
        [0, 0],
        [100, 100],
        [100, 0],
        [0, 100],
    ])('mouse move to x: %d, y: %d', (xArgs: number, yArgs: number) => {
        fireEvent.pointerMove(window, {clientX: xArgs, clientY: yArgs});

        expect(x.value).toBe(xArgs);
        expect(y.value).toBe(yArgs);
        expect(isDragging.value).toBe(true);
    });
});

describe('DragAndDropComposables.useDrop', () => {
    it('call drop on drop', () => {
        let drop = vi.fn();
        TestHelper.withSetup(() => DragAndDropComposables.useDrop('test', dropDiv, drop));

        fireEvent.pointerDown(dragDiv, {clientX: 0, clientY: 0});
        TestHelper.moveMouse(10, 10);
        fireEvent.pointerUp(window, {clientX: 10, clientY: 10});

        expect(drop).toHaveBeenCalledWith({test: 0});
    });
    it('don\'t call drop on if not in rect', () => {
        let drop = vi.fn();
        TestHelper.withSetup(() => DragAndDropComposables.useDrop('test', dropDiv, drop));

        fireEvent.pointerDown(dragDiv, {clientX: 0, clientY: 0});
        TestHelper.moveMouse(110, 110);
        fireEvent.pointerUp(window, {clientX: 110, clientY: 110});

        expect(drop).not.toHaveBeenCalled();
    });
    it('don\'t call drop on drop with different type', () => {
        let drop = vi.fn();
        TestHelper.withSetup(() => DragAndDropComposables.useDrop('test2', dropDiv, drop));

        fireEvent.pointerDown(dragDiv, {clientX: 0, clientY: 0});
        TestHelper.moveMouse(10, 10);
        fireEvent.pointerUp(window, {clientX: 10, clientY: 10});

        expect(drop).not.toHaveBeenCalled();
    });
    it('don\'t call drop if unmounted', () => {
        let drop = vi.fn();
        const {app} = TestHelper.withSetup(() => DragAndDropComposables.useDrop('test', dropDiv, drop));

        app.unmount();

        fireEvent.pointerDown(dragDiv, {clientX: 0, clientY: 0});
        TestHelper.moveMouse(10, 10);
        fireEvent.pointerUp(window, {clientX: 10, clientY: 10});

        expect(drop).not.toHaveBeenCalled();
    });
});
