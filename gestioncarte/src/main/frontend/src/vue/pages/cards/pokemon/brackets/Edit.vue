<template>
    <div class="container">
        <BracketEditForm v-model="bracket" @save="save()" />
    </div>
</template>

<script lang="ts" setup>
import {computed, ref, watchEffect} from "vue";
import {BracketDTO} from "@/types";
import {useRoute, useRouter} from "vue-router";
import rest from "@/rest";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {BracketEditForm} from "@components/cards/pokemon/bracket";
import {useRaise} from "@/alert";

const route = useRoute();
const router = useRouter();
const raise = useRaise();

const bracketId = computed(() => route.params.bracketId as string || '');
const bracket = ref<BracketDTO>({
    translations: {},
    id: "",
    name: "",
    localization: "us",
    bold: false
});

usePageTitle(computed(() => {
    const edit = bracketId.value;
    const end = edit ? ` (id: ${bracketId.value})` : '';

    return `Pokémon - ${edit? 'Modification' : 'Ajout'} de crochet${end}`;
}));

const save = async () => {
    let id = bracketId.value;

    if (!id) {
        id = await rest.post('/api/cards/pokemon/brackets', {
            data: bracket.value,
        });
    } else {
        await rest.put('/api/cards/pokemon/brackets', {
            data: bracket.value,
        });
    }
    raise.success('Crochet enregistrée avec succès');
    await router.push(`/cards/pokemon/brackets/${id}`);
};
watchEffect(async () => {
    if (bracketId.value) {
        bracket.value = await rest.get(`/api/cards/pokemon/brackets/${bracketId.value}`);
    }
});
</script>
