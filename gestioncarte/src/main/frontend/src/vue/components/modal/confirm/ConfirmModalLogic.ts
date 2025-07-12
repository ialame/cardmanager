import {VNode} from "vue";

export namespace ConfirmModalLogic {

    export type ModalContentType = VNode | string;

    export type ModalOpen = (m: ModalContentType) => Promise<void>;

    let modalOpen: ModalOpen | undefined = undefined;

    export const registerConfirmModal = (open: ModalOpen) => {
        modalOpen = open;
    };

    export const confirm = async (message: ModalContentType) => {
        if (modalOpen) {
            return modalOpen(message);
        }
        return Promise.reject("No confirm modal registered");
    }
}
