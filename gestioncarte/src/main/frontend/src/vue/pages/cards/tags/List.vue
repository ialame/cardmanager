<template>
    <SideButtons>
        <ScrollToTop />
    </SideButtons>
    <div class="container" v-if="!isEmpty(cardTags)">
        <table class="table table-striped mt-4">
            <thead>
                <tr>
                    <th scope="col">Id</th>
                    <th scope="col">Type</th>
                    <th scope="col" class="min-w-200">Nom</th>
                    <th scope="col" class="min-w-100"></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="tag in cardTags" :key="tag.id">
                    <td>{{ tag.id }}</td>
                    <td>{{ tag.type }}</td>
                    <td>{{ tag.translations.us?.name || tag.translations.jp?.name || '' }}</td>
                    <td>
                        <div class="float-end">
                            <FormButton color="link" title="Modifier la carte" @Click="editTag(tag.id)" class="me-2 p-0 no-focus">
                                <Icon name="pencil" />
                            </FormButton>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script lang="ts" setup>
import {isEmpty} from 'lodash';
import {ScrollToTop, SideButtons} from '@components/side';
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRouter} from "vue-router";
import {cardTagService} from "@components/cards/tags";

usePageTitle("Tags des cartes");

const router = useRouter();
const cardTags = cardTagService.all;
const editTag = (id: string) => {
    if (id) {
        router.push(`/cards/tags/${id}`);
    }
};

</script>
