<template>
    <SideButtons>
        <ScrollToTop />
    </SideButtons>
    <div class="container" v-if="!isEmpty(brackets)">
        <table class="table table-striped mt-4">
            <thead>
                <tr>
                    <th scope="col">Id</th>
                    <th scope="col" class="min-w-200">Nom</th>
                    <th scope="col">Charset</th>
                    <th scope="col" class="min-w-100"></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="bracket in brackets" :key="bracket.id">
                    <td>{{ bracket.id }}</td>
                    <td>{{ bracket.name }}</td>
                    <td><Flag :lang="bracket.localization" /></td>
                    <td>
                        <div class="float-end">
                            <FormButton color="link" title="Modifier le crochet" @Click="editBracket(bracket.id)" class="me-2 p-0 no-focus">
                                <Icon name="pencil" />
                            </FormButton>
                            <FormButton color="link" title="Supprimer le crochet" @Click="deleteBracket(bracket.id)" class="me-2 p-0 no-focus">
                                <Icon class="text-danger" name="trash"  />
                            </FormButton>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script lang="ts" setup>
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {isEmpty} from 'lodash';
import {ScrollToTop, SideButtons} from '@components/side';
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRouter} from "vue-router";
import {Flag} from "@/localization";
import {ConfirmComposables} from "@/vue/composables/ConfirmComposables";
import {h} from "vue";
import {invoke} from "@vueuse/core";
import confirmOrCancel = ConfirmComposables.confirmOrCancel;


usePageTitle("Pokémon - Liste des crochets");

const router = useRouter();
const brackets = PokemonComposables.bracketService.all;
const editBracket = (id: string) => {
    if (id) {
        router.push(`/cards/pokemon/brackets/${id}`);
    }
};

const getContMessage = (count: number) => h('div', {class: 'container'}, [
    h('span', {class: 'row'}, invoke(() => {
        if (count === 0) {
            return 'Acune cartes ne sont associées à ce crochet.';
        } else if (count === 1) {
            return 'Une carte est associée à ce crochet.';
        } else {
            return `${count} cartes sont associées à ce crochet.`;
        }
    })),
    h('span', {class: 'row'}, `Voulez-vous le supprimer ?`)
]);
const deleteBracket = async (id: string) => {
    if (id && await confirmOrCancel(getContMessage((await PokemonComposables.bracketService.findAllCards(id)).length))) {
        await PokemonComposables.bracketService.delete(id);
    }
};

</script>
