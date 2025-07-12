<template>
    <SideButtons>
        <ScrollToTop />
    </SideButtons>
    <div class="container" v-if="!isEmpty(filteredVersions)">
        <table class="table table-striped mt-4">
            <thead>
                <tr>
                    <th scope="col">Nom </th>
                    <th scope="col" class="min-w-100"></th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="version in filteredVersions" :key="version.id">
                    <td>
                        <PromoCardVersionLabel :version="version" />
                    </td>
                    <td>
                        <div class="float-end">
                            <FormButton color="link" title="Modifier la version" @Click="editVersion(version.id)" class="me-2 p-0 no-focus">
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
import {promoCardVersionService} from "@components/cards/promo";
import PromoCardVersionLabel from "@components/cards/promo/version/PromoCardVersionLabel.vue";
import {getTcg, useTcg} from "@/tcg";
import {computed} from "vue";

usePageTitle("Liste des versions de promotion");

const tcg = useTcg();
const router = useRouter();
const versions = promoCardVersionService.all;
const filteredVersions = computed(() => versions.value.filter(v => !tcg.value || getTcg(v.tcg) === tcg.value));

const editVersion = (id: string) => {
    if (id) {
        router.push(`/cards/${tcg.value}/promos/versions/${id}`);
    }
};

</script>
