<template>
    <FormButton v-if="!icon" color="danger" title="Vider la liste" @Click="clearCards" :disabled="extractedCardsCount === 0">Vider la liste</FormButton>
    <FormButton v-else color="danger" title="Vider la liste" class="round-btn-sm" @Click="clearCards" :disabled="extractedCardsCount === 0">
        <Icon name="trash" />
    </FormButton>
</template>

<script lang="ts" setup>
import FormButton from "@components/form/FormButton.vue";
import {computed} from "vue";
import {useRaise} from "@/alert";
import {ConfirmComposables} from "@/vue/composables/ConfirmComposables";
import Icon from "@components/Icon.vue";
import {extractedYuGiOhCardsService} from "@components/cards/yugioh/extracted/list/service";
import confirmOrCancel = ConfirmComposables.confirmOrCancel;

interface Props {
    icon?: boolean;
}

interface Emits {
    (e: 'clear'): void;
}

withDefaults(defineProps<Props>(), {
    icon: false,
});
const emit = defineEmits<Emits>();
const raise = useRaise();
const extractedCardsCount = computed(() => extractedYuGiOhCardsService.all.value.length);

const clearCards = async () => {
    if (await confirmOrCancel("Voulez-vous vraiment vider la liste des cartes en attente ?")) {
        await extractedYuGiOhCardsService.clear();
        emit('clear');
        raise.success('Cartes effacées avec succès');
    }
}
</script>
