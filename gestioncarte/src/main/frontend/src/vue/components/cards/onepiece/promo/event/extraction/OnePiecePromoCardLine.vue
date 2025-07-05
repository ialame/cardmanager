<template>
    <tr>
        <td>
            <div v-if="set" class="d-flex flex-row">
                <OnePieceSetLabel :set="set" />
            </div>
        </td>
        <td>
            <template v-if="card">
                <div class="d-flex flex-row">
                    <OnePieceCardLabel :card="card" :localization="promo.localization" />
                </div>
            </template>
            <span v-else>!!! CARTE MANQUANTE !!!</span>
        </td>
        <td>{{ promo.name }}</td>
        <td>
            <PromoCardVersionLabel v-if="promo.versionId" :version="promo.versionId" />
        </td>
        <td>
            <FormButton color="link" @click="emit('remove')"><Icon name="trash" class="text-danger" /></FormButton>
        </td>
    </tr>
</template>

<script lang="ts" setup>
import {PromoCardDTO} from "@/types";
import {PromoCardVersionLabel} from "@components/cards/promo/version";
import {computedAsync} from "@vueuse/core";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {onePieceSetService} from "@components/cards/onepiece/set";
import OnePieceSetLabel from "@components/cards/onepiece/set/OnePieceSetLabel.vue";
import OnePieceCardLabel from "@components/cards/onepiece/OnePieceCardLabel.vue";
import {onePieceCardService} from "@components/cards/onepiece/service";

interface Props {
    promo: PromoCardDTO;
}
interface Events {
    (e: 'remove'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Events>();

const card = computedAsync(() => onePieceCardService.findWithPromo(props.promo.id));
const set = computedAsync(async () => card.value ? await onePieceSetService.get(card.value.setIds[0]) : undefined);
</script>
