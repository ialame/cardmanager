import {WorkflowComposables} from "@/vue/composables/WorkflowComposables";
import {describe, expect, it} from 'vitest';

const testEntries = [
    {index: 0},
    {index: 1},
    {index: 2},
    {index: 3},
    {index: 4},
    {index: 5},
];

describe('WorkflowComposables.useWorkflow', () => {
    it('should have index at 0 on next', () => {
        const workflow = WorkflowComposables.useWorkflow(testEntries)

        workflow.next();
        expect(workflow.index.value).toBe(0);
        expect(workflow.current.value).toBe(testEntries[0]);
    });
    it('should have index at 4 on next with 4 as argument', () => {
        const workflow = WorkflowComposables.useWorkflow(testEntries)

        workflow.next(4);
        expect(workflow.index.value).toBe(4);
        expect(workflow.current.value).toBe(testEntries[4]);
    });
    it('should have index at undefined after reset', () => {
        const workflow = WorkflowComposables.useWorkflow(testEntries)

        workflow.next(4);
        workflow.reset()
        expect(workflow.index.value).toBe(undefined);
        expect(workflow.current.value).toBe(undefined);
    });
})
