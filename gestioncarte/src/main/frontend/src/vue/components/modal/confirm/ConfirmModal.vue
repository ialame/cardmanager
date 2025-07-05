<template>
    <Modal ref="modal">
        <div v-if="isString(message)">{{message}}</div>
        <component v-else-if="!isNil(message) &&!isEmpty(message)" :is="message" />
        <div class="mt-2">
            <div class="float-end">
                <FormButton color="primary" @click="callbacks?.confirm?.()">Confirmer</FormButton>
                <FormButton color="secondary" class="ms-2" @click="callbacks?.cancel?.()">Annuler</FormButton>
            </div>
        </div>
    </Modal>
</template>

<script lang="ts" setup>
import Modal from "@components/modal/Modal.vue";
import {ConfirmModalLogic} from "@components/modal/confirm/ConfirmModalLogic";
import {onMounted, ref} from "vue";
import FormButton from "@components/form/FormButton.vue";
import {isEmpty, isNil, isString} from "lodash";
import ModalContentType = ConfirmModalLogic.ModalContentType;

type Callbacks = {
    confirm?: () => void;
    cancel?: () => void;
}

const message = ref<ModalContentType>("");
const modal = ref<typeof Modal>();
const callbacks = ref<Callbacks>({});

const open = (m: ModalContentType) => {
    callbacks.value?.cancel?.();
    message.value = m;
    modal.value?.show();
    return new Promise<void>((resolve, reject) => {
        callbacks.value = {
            confirm: () => {
                modal.value?.hide();
                callbacks.value = {};
                resolve();
            },
            cancel: () => {
                modal.value?.hide();
                callbacks.value = {};
                reject();
            }
        };
    });
}

onMounted(() => ConfirmModalLogic.registerConfirmModal(open));
</script>
