<template>
    <ul class="ps-0 ms-0">
        <Collapse open tag="li" label="Cartes">
            <ul>
                <SideBarLink to="/cards/onepiece/extraction" label="Lancer une extraction" />
                <SideBarLink to="/cards/onepiece/extracted" label="Cartes en attente" />
                <SideBarLink to="/cards/onepiece/new" label="Ajout manuel" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Extensions">
            <ul>
                <SideBarLink to="/cards/onepiece/sets/new" label="Ajout manuel" />
                <SideBarLink to="/cards/onepiece/sets/unsaved" label="Nouvelles extension" />
                <SideBarLink to="/cards/onepiece/sets" label="List" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Series">
            <ul>
                <SideBarLink to="/cards/onepiece/series/new" label="Ajout manuel" />
            </ul>
        </Collapse>
        <Collapse open tag="li" label="Promo">
            <ul>
                <Collapse open tag="li" label="Evenement">
                    <ul>
                        <SideBarLink to="/cards/onepiece/promos/events/new" label="Ajout" />
                        <SideBarLink to="/cards/onepiece/promos/events/extracted" label="Promos sans évènement" />
                        <Collapse open tag="li" label="Caracteristiques">
                            <ul>
                                <SideBarLink to="/cards/onepiece/promos/events/traits" label="Liste" />
                                <SideBarLink to="/cards/onepiece/promos/events/traits/new" label="Ajout" />
                            </ul>
                        </Collapse>
                    </ul>
                </Collapse>
                <Collapse open tag="li" label="Versions">
                    <ul>
                        <SideBarLink to="/cards/onepiece/promos/versions" label="Liste" />
                        <SideBarLink to="/cards/onepiece/promos/versions/new" label="Ajout" />
                    </ul>
                </Collapse>
            </ul>
        </Collapse>
    </ul>
</template>

<script lang="ts" setup>
import Collapse from '@components/collapse/Collapse.vue';
import SideBarLink from "@components/sidebar/SideBarLink.vue";</script>
