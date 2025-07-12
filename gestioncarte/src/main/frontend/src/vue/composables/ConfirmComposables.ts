import {ConfirmModalLogic} from "@components/modal/confirm/ConfirmModalLogic";

export namespace ConfirmComposables {
    export const confirm = ConfirmModalLogic.confirm;

    export const confirmOrCancel = async (message: ConfirmModalLogic.ModalContentType) => {
        try {
            await confirm(message);
            return true;
        } catch (e) {
            return false;
        }
    }
}
