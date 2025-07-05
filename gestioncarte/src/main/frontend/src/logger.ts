import log from "loglevel";
import {envName} from "@/utils";
import {apply, reg} from "loglevel-plugin-prefix";
import {isEmpty} from "lodash";

export function initLogger(worker?: string) {
    log.setLevel(envName === 'production' ? 'info' : 'trace');
    reg(log);
    if (!isEmpty(worker)) {
        apply(log, { template: '[%t] %l:(' + worker + '):' });
    } else {
        apply(log, {template: '[%t] %l:'});
    }
}
