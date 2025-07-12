<template>
    <AdvancedFormSelect :path="path" :values="cardTags" #default="{ value: tag }">
        {{tag?.translations?.us?.name || ''}} ({{tag?.type || ''}}) <IdTooltip :id="tag?.id"/>
    </AdvancedFormSelect>
    <div v-if="!value?.id && !readOnly" class="ms-2 d-flex flex-column">
        <FormButton color="secondary" class="form-btn" @click="edit()"><Icon name="pencil"/></FormButton>
    </div>
    <Modal ref="editModal" label="Tag de carte" dynamic>
        <CardTagsEditForm v-model="edited" @save="save()" />
    </Modal>
</template>

<script lang="ts" setup>
import {AdvancedFormSelect, useAdvancedFormInput} from "@components/form/advanced";
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {CardTagsEditForm, cardTagService} from "@components/cards/tags";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import Modal from "@components/modal/Modal.vue";
import {ref, watchEffect} from "vue";
import {CardTagDTO} from "@/types";
import {Path} from "@/path";

interface Props {
    path: Path;
}

const props = defineProps<Props>();

const cardTags = cardTagService.all;
const { value, readOnly } = useAdvancedFormInput<any, CardTagDTO>(() => props.path);

const editModal = ref<typeof Modal>();
const edited = ref<CardTagDTO>();
const edit = () => {
    edited.value = value.value;
    editModal.value?.show();
}
const save = async () => {
    if (edited.value) {
        value.value = await cardTagService.save(edited.value);
    }
    editModal.value?.hide();
}
watchEffect(async () => {
    if (value.value && !value.value.id) {
        const type = value.value.type;
        const name = value.value.translations?.us?.name;

        if (type && name) {
            const tags = await cardTagService.findAllByTypeAndName(type, name);

            if (tags.length > 0) {
                value.value = tags[0];
            }
        }
    }
});
</script>
