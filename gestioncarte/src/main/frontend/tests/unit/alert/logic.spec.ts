import {useAlerts, useRaise} from "@/alert";
import {describe, expect, it, vi} from 'vitest';

vi.useFakeTimers();

describe('useRaise', () => {
    const alerts = useAlerts();

    it.each([
        ['foo'],
        ['bar']
    ])('should raise success alert with message %s', (message: string) => {
        const raise = useRaise();

        raise.success(message);

        expect(alerts.value[0]).toEqual({type: 'success', message});
        vi.runAllTimers();
        expect(alerts.value.length).toBe(0);
    });
    it.each([
        ['foo'],
        ['bar']
    ])('should raise info alert with message %s', (message: string) => {
        const raise = useRaise();

        raise.info(message);

        expect(alerts.value[0]).toEqual({type: 'info', message});
        vi.runAllTimers();
        expect(alerts.value.length).toBe(0);
    });
    it('should not push 2 times the same message', () => {
        const raise = useRaise();

        raise.info('foo');
        raise.info('foo');

        expect(alerts.value.length).toBe(1);
        vi.runAllTimers();
        expect(alerts.value.length).toBe(0);
    });
});
