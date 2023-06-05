import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {deleteBuilding, getAllTypes} from "../../services/BuildingService";
import {MaterialReactTable} from "material-react-table";
import {Box, Button, IconButton, Tooltip} from "@mui/material";
import {Delete} from "@mui/icons-material";
import CreateModal from "../../components/UI/CreateModal";

const BuildingTypes = () => {
    const columns = useMemo(() => [
        {
            header: "ID",
            accessorKey: "id",
        },
        {
            header: "Название",
            accessorKey: "name",
        }
    ], []);

    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [types, setTypes] = useState([]);

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure you want to delete ${row.getValue("name")}`)) {
                return;
            }
            deleteBuilding(row.getValue("id")).then(() => refreshData());
        },
        [],
    );

    const refreshData = () => {
        getAllTypes().then(types => {
            setTypes(types);
            console.log(types);
        })
    };


    useEffect(() => {
        refreshData();
    }, [])

    return (
        <div>
            <div>
                <h2>Виды сооружений</h2>
                <MaterialReactTable
                    displayColumnDefOptions={{
                        'mrt-row-actions': {
                            size: 60,
                        },
                    }}
                    columns={columns}
                    data={types}
                    renderRowActions={({row, table}) => (
                        <Box sx={{display: 'flex', gap: '1rem'}}>
                            <Tooltip arrow placement="right" title="Delete">
                                <IconButton color="error" onClick={() => handleDeleteRow(row)}>
                                    <Delete/>
                                </IconButton>
                            </Tooltip>
                        </Box>
                    )}
                />
            </div>
        </div>
    );
};

export default BuildingTypes;
