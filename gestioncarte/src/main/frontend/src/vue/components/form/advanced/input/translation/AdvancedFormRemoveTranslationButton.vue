<template>
    <FormButton v-if="!fixedLocalizations" color="danger" class="form-btn ms-2" @click.stop="remove()">
        <Icon name="trash" />
    </FormButton>
</template>

<script lang = "ts" setup>
import {useAdvancedFormInput} from "@components/form/advanced";
import {LocalizationCode} from "@/localization";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {Path} from "@/path";
import {useAdvancedFormTranslationContext} from "@components/form/advanced/input/translation/logic";

interface Props {
    path: Path;
    localization: LocalizationCode;
}
interface Emits {
    (e: 'remove'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const { fixedLocalizations, removeConfirmation } = useAdvancedFormTranslationContext();
const { reviewable, reviewed } = useAdvancedFormInput<any, any>(() => props.path);

const remove = async () => {
    if (removeConfirmation.value) {
        const confirmed = await removeConfirmation.value(props.localization);

        if (!confirmed) {
            return;
        }
    }
    emit('remove');
    if (reviewable.value) {
        reviewed.value = true;
    }
};
</script>
