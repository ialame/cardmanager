import {createGlobalState} from "@vueuse/core";
import {ref} from 'vue';
import {isNil, remove} from "lodash";
import log from "loglevel";


export type AlertType = 'danger' | 'warning' | 'info' | 'success';

export type Alert = {
    type: AlertType,
    message: string
};

export const useAlerts = createGlobalState(() => ref<Alert[]>([]));

export const useRaise = createGlobalState(() => {
    const alerts = useAlerts();

    const raise = (type: AlertType, message: string) => {
        if (isNil(type) || isNil(message) || alerts.value.some(alert => alert.type === type && alert.message === message)) {
            return;
        }

        const alert = {type, message};

        alerts.value.push(alert);
        log.debug("Alert raised", alert);
        setTimeout(() => {
            if (alerts.value.includes(alert)) {
                remove(alerts.value, alert);
            }
        }, 5000);
    }

    raise.success = (m: string) => raise('success', m);
    raise.info = (m: string) => raise('info', m);
    raise.warn = (m: string) => raise('warning', m);
    raise.error = (m: string) => raise('danger', m);
    raise.genericError = () => raise.error("Une erreur est survenue lors du chargement de la page.");
    return raise;
});
