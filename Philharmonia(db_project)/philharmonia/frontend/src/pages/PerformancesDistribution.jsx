import React, {useCallback, useEffect, useMemo, useState} from 'react';
import {MaterialReactTable} from "material-react-table";
import {Box, Button, IconButton, Tooltip} from "@mui/material";
import {Delete, Edit} from "@mui/icons-material";
import CreateModal from "../components/UI/CreateModal";
import {
    addPerformanceDistribution,
    deletePerformanceDistribution,
    getPerformanceDistribution
} from "../services/PerformanceService";

const PerformancesDistribution = () => {
    const columns = useMemo(() => [
        {
            header: "Имя",
            accessorKey: "artist.name",
        },
        {
            header: "Фамилия",
            accessorKey: "artist.surname",
        },
        {
            header: "Выступление",
            accessorKey: "performance.name",
        }
    ], []);

    const [distribution, setDistribution] = useState([]);
    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [validationErrors, setValidationErrors] = useState({});


    const handleCreateNewRow = (values) => {
        console.log(values);
        const data = {
            id: {},
            artist: {
                name: values["artist.name"],
                surname: values["artist.surname"],
            },
            performance: {
                name: values["performance.name"],
            },
        };
        addPerformanceDistribution(data).then(() => refreshData());
    };

    const handleDeleteRow = useCallback(
        (row) => {
            if (!confirm(`Are you sure?`)) {
                return;
            }
            const data = {
                id: {},
                artist: {
                    name: distribution[row.index]["artist.name"],
                    surname: distribution[row.index]["artist.surname"],
                },
                performance: {
                    name: distribution[row.index]["performance.name"],
                },
            };
            console.log(data);
            deletePerformanceDistribution(data).then(() => refreshData());
        },
        [],
    );

    const refreshData = () => {
        getPerformanceDistribution().then(distribution => {
            setDistribution(distribution);
            console.log(distribution);
        })
    };

    useEffect(() => {
        refreshData();
    }, [])

    return (
        <div>
            <div>
                <MaterialReactTable
                    displayColumnDefOptions={{
                        'mrt-row-actions': {
                            size: 60,
                        },
                    }}
                    columns={columns}
                    data={distribution}
                    enableEditing={true}
                    renderRowActions={({row, table}) => (
                        <Box sx={{display: 'flex', gap: '1rem'}}>
                            <Tooltip arrow placement="right" title="Delete">
                                <IconButton color="error" onClick={() => handleDeleteRow(row)}>
                                    <Delete/>
                                </IconButton>
                            </Tooltip>
                        </Box>
                    )}
                    renderTopToolbarCustomActions={() => (
                        <Button
                            color="secondary"
                            onClick={() => setCreateModalOpen(true)}
                            variant="contained"
                        >
                            Добавить связь выступления
                        </Button>
                    )}
                />
            </div>
            <CreateModal
                columns={columns}
                open={createModalOpen}
                onClose={() => setCreateModalOpen(false)}
                onSubmit={handleCreateNewRow}
            />
        </div>
    );
};

export default PerformancesDistribution;
