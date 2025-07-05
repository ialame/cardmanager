<template>
    <div class="container mt-4">
        <FormRow>
            <YuGiOhSetSearch v-model="set" />
        </FormRow>
        <FormRow>
            <Column>
                <div class="d-flex flex-row float-end">
                    <FormButton color="primary" @click="list">Lister les cartes</FormButton>
                </div>
            </Column>
        </FormRow>
    </div>
</template>

<script lang="ts" setup>
import {YuGiOhSetSearch} from "@components/cards/yugioh/set";
import {YuGiOhSetDTO} from "@/types";
import {ref} from "vue";
import FormButton from "@components/form/FormButton.vue";
import FormRow from "@components/form/FormRow.vue";
import Column from "@components/grid/Column.vue";
import {useRouter} from "vue-router";
import {useRaise} from "@/alert";

const raise = useRaise();
const router = useRouter();

const set = ref<YuGiOhSetDTO>();

const list = () => {
    if (set.value && set.value.id) {
        router.push(`/cards/yugioh/sets/${set.value.id}/cards`);
    } else {
        raise.warn("Aucune extension selectionée.");
    }
}
</script>
